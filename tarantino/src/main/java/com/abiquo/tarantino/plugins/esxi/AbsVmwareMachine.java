/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package com.abiquo.tarantino.plugins.esxi;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.tarantino.plugins.esxi.utils.EsxiUtils;
import com.abiquo.tarantino.plugins.esxi.utils.VmwareMachineBasics;
import com.abiquo.tarantino.virtualmachine.IVirtualMachine;
import com.abiquo.util.ExtendedAppUtil;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.VmwareHypervisor;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.config.VmwareHypervisorConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.GenericVmConfigFault;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author pnavarro
 */
public abstract class AbsVmwareMachine implements IVirtualMachine
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(AbsVmwareMachine.class);

    protected String networkUUID = UUID.randomUUID().toString();

    /** Identifier of the machine */
    private final String uuid;

    /** Self virtual machine managed object reference. */
    private ManagedObjectReference _virtualMachine;

    private VmwareHypervisor hypervisor;

    /**
     * The standard constructor
     * 
     * @param configuration the virtual machine configuration
     * @throws VirtualMachineException
     */
    public AbsVmwareMachine(
        com.abiquo.commons.amqp.impl.datacenter.domain.jobs.VirtualMachine vmdef,
        VmwareHypervisor hypervisor)
    {
        // super(configuration);

        this.hypervisor = hypervisor;
        uuid = vmdef.getMachineID();
    }

    public void deployMachine()// throws VirtualMachineException
    {
        try
        {
            // if (!apputil.getServiceConnection3().isConnected())
            // TODO hypervisor.connectAndLogin(hconn)

            if (!hypervisor.getUtilsEsxi().isVMAlreadyCreated(uuid))
            {
                configureNetwork();

                // Create the template vdestPathirtual machine
                createVirtualMachine();

                // Stateless image located on the Enterprise Repository require to be copy on the
                // local fs.
                if (vmConfig.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
                {
                    // Copy from the NAS to the template virtual machine
                    cloneVirtualDisk();
                }

                // Attach the initial extended disks
                initDisks();

                // reconfigureNetwork();

            }

            // TODO The method areDisksAlreadyDeployed is not used to check if the disks are already
            // deployed

            checkIsCancelled();
        }
        catch (Exception e)
        {
            logger.error("Failed to deploy machine :{}", e);
            // The roll back in the virtual machine is done in top level when rolling back the
            // virtual appliance
            rollBackVirtualMachine();
            state = State.CANCELLED;
            throw new VirtualMachineException(e);
        }
        finally
        {
            utils.logout();
        }

        logger.info("Created vmware machine name:" + config.getMachineName() + "\t ID:"
            + config.getMachineId().toString() + "\t " + "using hypervisor connection at "
            + config.getHyper().getAddress().toString());

        state = State.DEPLOYED;
    }

    /**
     * Attach the initial extended disk on configuration
     */
    private void initDisks() throws VirtualMachineException
    {
        VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
        VirtualDeviceConfigSpec[] vdiskSpec;

        try
        {
            _virtualMachine = utils.getVmMor(machineName);

            vdiskSpec = disks.initialDiskDeviceConfigSpec();

            if (vdiskSpec != null)
            {
                logger.debug("Adding [{}] initial extended disks", vdiskSpec.length);
                vmConfigSpec.setDeviceChange(vdiskSpec);
            }
            else
            {
                logger.debug("Any disk configruation to add");
            }

            ManagedObjectReference tmor =
                utils.getService().reconfigVM_Task(_virtualMachine, vmConfigSpec);

            utils.monitorTask(tmor);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not initialize the extended disks", e);
        }
    }

    /**
     * Perform the virtual image cloning. Creates a copy of the original image and put it on where
     * the current hypervisor expects to load it.
     */
    protected void cloneVirtualDisk() throws VirtualMachineException
    {
        disks.moveVirtualDiskToDataStore();
    }

    /**
     * Private helper to create a virtual machine template from the open virtualization format
     * parameters
     * 
     * @throws Exception
     */
    private void createVirtualMachine() throws VirtualMachineException
    {
        String dcName; // datacenter name
        ManagedObjectReference dcmor; // datacenter
        ManagedObjectReference hfmor; // host folder
        ManagedObjectReference hostmor;// host
        ArrayList<ManagedObjectReference> crmors;// all computer resources on host folder
        ManagedObjectReference crmor; // computer resource
        VirtualMachineConfigSpec vmConfigSpec; // virtual machine configuration
        VirtualDeviceConfigSpec[] vdiskSpec; // disk configuration

        try
        {

            dcName = utils.getOption("datacentername");
            dcmor =
                utils.getAppUtil().getServiceUtil().getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                String message = "Datacenter " + dcName + " not found.";
                logger.error(message);
                throw new VirtualMachineException(message);
            }

            hfmor = utils.getAppUtil().getServiceUtil().getMoRefProp(dcmor, "hostFolder");

            hostmor = utils.getHostSystemMor(dcmor, hfmor);

            crmors = getAllComputerResourcesOnHostFolder(hfmor);

            crmor = utils.getComputerResourceFromHost(crmors, hostmor);

            // TODO #createVMConfigSpec defines not convenient default data, change this
            vmConfigSpec = configureVM(crmor, hostmor);

            logger.info("Machine name :{} Machine ID: {} ready to be created", machineName,
                config.getMachineId());

            ManagedObjectReference resourcePool =
                utils.getAppUtil().getServiceUtil().getMoRefProp(crmor, "resourcePool");
            ManagedObjectReference vmFolderMor =
                utils.getAppUtil().getServiceUtil().getMoRefProp(dcmor, "vmFolder");

            ManagedObjectReference taskmor =
                utils.getService().createVM_Task(vmFolderMor, vmConfigSpec, resourcePool, hostmor);

            /*
             * TODO ing //customizationIPSettings for the deploy CustomizationSpec customMachine =
             * setCustomizationSpec(); //setting the variable _virtualMachine
             * getVmMor(this.machineName);
             * apputil.getServiceConnection3().getService().customizeVM_Task(_virtualMachine,
             * customMachine);
             */

            utils.checkTaskState(taskmor);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not create the VM:" + e.getCause().getMessage(),
                e);
        }

    }

    @SuppressWarnings("unchecked")
    private ArrayList<ManagedObjectReference> getAllComputerResourcesOnHostFolder(
        final ManagedObjectReference hfmor) throws Exception
    {
        return utils.getAppUtil().getServiceUtil().getDecendentMoRefs(hfmor, "ComputeResource");
    }

    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        try
        {
            utils.reconnect();
            // Force to power off the machine before deleting
            powerOffMachine();

            // affect all the VM for the given machine name
            ServiceInstance si = utils.getAppUtil().getServiceInstance();

            Folder rootFolder = si.getRootFolder();

            executeTaskOnVM(VMTasks.DELETE);

            // Deconfigure networking resources

            try
            {
                deconfigureNetwork();
            }
            catch (Exception e)
            {
                logger.error(
                    "An error was occurred then deconfiguring the networking resources: {}", e);
            }
        }
        finally
        {
            utils.logout();
        }

        logger.debug("Deleted machine [{}]", machineName);
    }

    @Override
    public void reconfigVM(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        ResourceAllocationInfo raRAM;
        ResourceAllocationInfo raCPU;

        VirtualMachineConfigSpec vmConfigSpec;
        VirtualDeviceConfigSpec[] vdiskSpec = null;

        utils.reconnect();

        try
        {
            _virtualMachine = utils.getVmMor(machineName);
            vmConfigSpec = new VirtualMachineConfigSpec();

            // Setting the new Ram value
            if (newConfiguration.isRam_set())
            {
                logger.info("Reconfiguring The Virtual Machine For Memory Update " + machineName);

                vmConfigSpec.setMemoryMB(newConfiguration.getMemoryRAM() / 1048576);

            }

            // Setting the number cpu value
            if (newConfiguration.isCpu_number_set())
            {
                logger.info("Reconfiguring The Virtual Machine For CPU Update " + machineName);

                vmConfigSpec.setNumCPUs(newConfiguration.getCpuNumber());
            }

            // Setting the disk disk value
            // logger.info("Reconfiguring The Virtual Machine For disk Update " + machineName);

            vdiskSpec = disks.getDiskDeviceConfigSpec(newConfiguration);

            if (vdiskSpec != null)
            {
                vmConfigSpec.setDeviceChange(vdiskSpec);
            }
            else
            {
                logger.debug("Any disk configruation changed");
            }

            ManagedObjectReference tmor =
                utils.getService().reconfigVM_Task(_virtualMachine, vmConfigSpec);
            utils.monitorTask(tmor);
            // Updating configuration

            vmConfig = newConfiguration;
            disks.setVMConfig(vmConfig);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            utils.logout();
        }
    }

    /**
     * Used during creation sets the additional configuration into the VM.
     * 
     * @param computerResMOR, the computer resource related to the current VM.
     * @param hostMOR, the host related to the current VM.
     * @return a configuration containing the specified resources
     */
    public abstract VirtualMachineConfigSpec configureVM(ManagedObjectReference computerResMOR,
        ManagedObjectReference hostMOR) throws VirtualMachineException;

    /**
     * Private helper to check the real state of the virtual machine
     * 
     * @param stateToCheck the state to check
     * @return true if the state in the hypervisors equals to the state as parameter, false if
     *         contrary
     */
    private boolean checkState(final State stateToCheck) throws VirtualMachineException
    {
        return getStateInHypervisor().compareTo(stateToCheck) == 0;
    }

    @Override
    public void bundleVirtualMachine(final String sourcePath, final String destinationPath,
        final String snapshotName, final boolean isManaged) throws VirtualMachineException
    {
        try
        {
            utils.reconnect();

            disks.bundleVirtualDisk(sourcePath, destinationPath, snapshotName, isManaged);
        }
        finally
        {
            utils.logout();
        }
    }

    // /**
    // * Destroy a VM.
    // *
    // * @param vmMOR, a virtual machine related to ''machineName''
    // */
    // private void destroyVM(final ManagedObjectReference vmMOR) throws VirtualMachineException
    // {
    // ManagedObjectReference taskDestroy;
    //
    // String vmName = "unknow dynamic property ''name''";
    // try
    // {
    // vmName = (String) utils.getAppUtil().getServiceUtil().getDynamicProperty(vmMOR, "name");
    //
    // logger.info("Powering off virtualmachine '{}'", vmName);
    // }
    // catch (Exception e) // getDynamicProperty
    // {
    // logger.warn("Can not get the dynamic property 'name' for the VM [{}]",
    // vmMOR.get_value());
    // }
    //
    // try
    // {
    // taskDestroy = utils.getService().destroy_Task(vmMOR);
    //
    // utils.checkTaskState(taskDestroy);
    //
    // logger.info("VM {} powered off successfuly", vmName);
    // }
    // catch (Exception e)
    // {
    // throw new VirtualMachineException("Can not destroy the VM " + vmName, e);
    // }
    // }

    private String getMachineName(String machineName)
    {
        // The 4 last characters of the machine name are erased because are omitted by the ESXi
        // machine name in ESXi only has 32 chars.
        if (machineName.length() > 32)
        {
            return machineName.substring(0, 32);
        }
        else
        {
            return machineName;
        }
    }

}
