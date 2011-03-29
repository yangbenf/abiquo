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
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.japi.SideEffect;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.PrimaryDisk.DiskStandardConfiguration;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.PrimaryDisk;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.SecondaryDisks;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualNIC;
import com.abiquo.commons.amqp.impl.datacenter.domain.dto.SnapshotVirtualMachineDto;
import com.abiquo.tarantino.errors.VirtualFactoryError;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.plugins.esxi.utils.VmwareMachineBasics.VMTasks;
import com.abiquo.tarantino.utils.ReconfigureUtils;
import com.abiquo.tarantino.virtualmachine.IVirtualMachine;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author pnavarro
 */
public abstract class AbsVmwareMachine implements IVirtualMachine<VmwareHypervisorConnection>
{
    /** The logger */
    protected static final Logger logger = LoggerFactory.getLogger(AbsVmwareMachine.class);


    /**
     * Used during creation sets the additional configuration into the VM.
     * 
     * @param computerResMOR, the computer resource related to the current VM.
     * @param hostMOR, the host related to the current VM.
     * @return a configuration containing the specified resources
     */
    public abstract VirtualMachineConfigSpec configureVM(ManagedObjectReference computerResMOR,
        ManagedObjectReference hostMOR) throws VirtualFactoryException;
    
    

    /**
     * Private helper to create a virtual machine template from the open virtualization format
     * parameters
     * 
     * @throws VirtualFactoryException
     * @throws Exception
     */
    private void createVirtualMachine(VmwareHypervisorConnection hypervisor, String virtualMachineId)
        throws VirtualFactoryException // throws
    // VirtualMachineException
    {
        ManagedObjectReference dcmor; // datacenter
        ManagedObjectReference hfmor; // host folder
        ManagedObjectReference hostmor;// host
        ArrayList<ManagedObjectReference> crmors;// all computer resources on host folder
        ManagedObjectReference crmor; // computer resource
        VirtualMachineConfigSpec vmConfigSpec; // virtual machine configuration
        VirtualDeviceConfigSpec[] vdiskSpec; // disk configuration

        try
        {

            dcmor = hypervisor.getUtils().getDatacenterMor();

            hfmor = hypervisor.getUtils().getHostFolder();

            hostmor = hypervisor.getUtils().getHostSystemMor(dcmor, hfmor); // TODO on EsxiUtils

            crmors = hypervisor.getUtils().getDecendentMoRefs(hfmor, "ComputeResource");

            crmor = hypervisor.getUtils().getComputerResourceFromHost(crmors, hostmor);

            // TODO #createVMConfigSpec defines not convenient default data, change this
            vmConfigSpec = configureVM(crmor, hostmor);

            logger.info("Machine :{} ready to be created", virtualMachineId);

            ManagedObjectReference resourcePool =
                hypervisor.getUtils().getMoRefProp(crmor, "resourcePool");
            ManagedObjectReference vmFolderMor =
                hypervisor.getUtils().getMoRefProp(dcmor, "vmFolder");

            ManagedObjectReference taskmor =
                hypervisor.getUtils().getVimStub()
                    .createVM_Task(vmFolderMor, vmConfigSpec, resourcePool, hostmor);

            /*
             * TODO ing //customizationIPSettings for the deploy CustomizationSpec customMachine =
             * setCustomizationSpec(); //setting the variable _virtualMachine
             * getVmMor(this.machineName);
             * apputil.getServiceConnection3().getService().customizeVM_Task(_virtualMachine,
             * customMachine);
             */

            hypervisor.getUtils().checkTaskState(taskmor);

        }
        catch (VirtualFactoryException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryError.CREATE_VM, String.format(
                "Virtual Machine : %s" + "\nCaused by:%s", virtualMachineId, e.toString()));
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

    // //


    /*
     * INTERFACE IMPLEMENTATION *
     */

    @Override
    public boolean exist(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        return hypervisor.getUtils().isVMAlreadyCreated(vmUuid);
    }

    @Override
    public State getState(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {        
        final String vmUuid = vmdefinition.getMachineID();

        if (!exist(hypervisor, vmdefinition))
        {
            return State.UNDEPLOYED;
        }

        VirtualMachinePowerState hypervisorState = hypervisor.getUtils().getVmState(vmUuid);

        switch (hypervisorState)
        {
            case poweredOff:
                return State.OFF;

            case poweredOn:
                return State.ON;

            case suspended:
                return State.PAUSED;

            default:
                return State.UNKNOWN;
        }

        // TODO never return CONFIGURED
    }

    @Override
    public void doConfigure(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        if (exist(hypervisor, vmdefinition))
        {
            throw new VirtualFactoryException(VirtualFactoryError.VIRTUAL_MACHINE_ALREADY_EXIST,
                vmUuid);
        }

        // Configure the network
        List<VirtualNIC> nics = vmdefinition.getNetworkConfiguration().getVirtualNIC();
        hypervisor.getUtils().getUtilNetwork().configureNetwork(nics);

        // Create the template vdestPathirtual machine
        createVirtualMachine(hypervisor, vmUuid);

        // Stateless image located on the Enterprise Repository require to be copy on the local fs.
        DiskStandardConfiguration disk = vmdefinition.getPrimaryDisk().getDiskStandardConf();
        if (disk == null)
        {
            throw new VirtualFactoryException(VirtualFactoryError.COMMUNITY_ONLY_STATELESS_DISKS);
        }
        // TODO premium

        // Copy from the NAS to the template virtual machine Perform the virtual image cloning.
        // Creates a copy of the original image and put it on where the current hypervisor expects
        // to load it.
        hypervisor.getUtils().getUtilDisks().moveVirtualDiskToDataStore(disk, vmUuid);

        // Attach the initial extended disks
        // TODO premium initDisks(vmdef.getSecondaryDisks());
        // reconfigureNetwork();
        // TODO The method areDisksAlreadyDeployed is not used to check if the disks are already
        // deployed
        // TODO checkIsCancelled();
        // }
        // TODO rollback on the supervisor ??? TODO
        // catch (VirtualFactoryException vfe)
        // {
        // logger.error("Failed to configure machine, will rollback :{}", vfe);
        // // The roll back in the virtual machine is done in top level when rolling back the
        // virtual appliance
        //
        //
        // doDeconfigure(connection, vmdefinition);
        //
        // throw vfe;
        // }
    }

    // TODO is power off
    @Override
    public void doDeconfigure(VmwareHypervisorConnection hypervisor,
        VirtualMachineDefinition vmdefinition) throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        // TODO check VMTask.DELETE destroy the disk on destinationDatastore
        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.DELETE, vm);

        try
        {
            // Deconfigure networking resources
            List<VirtualNIC> nics = vmdefinition.getNetworkConfiguration().getVirtualNIC();
            hypervisor.getUtils().getUtilNetwork().deconfigureNetwork(nics);
        }
        catch (Exception e)
        {
            logger
                .error("An error was occurred then deconfiguring the networking resources: {}", e);
            // TODO do not block the operation if the network isn't deconfigured
        }
    }

    @Override
    public void doPowerOn(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {        
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.POWER_ON, vm);
    }

    @Override
    public void doPowerOff(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.POWER_OFF, vm);
    }

    @Override
    public void doReset(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.RESET, vm);
    }

    @Override
    public void doPause(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {     
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.PAUSE, vm);
    }

    @Override
    public void doResume(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        final String vmUuid = vmdefinition.getMachineID();

        VirtualMachine vm = hypervisor.getUtils().getVm(vmUuid);

        hypervisor.getUtils().getUtilBasics().executeTask(VMTasks.RESUME, vm);
    }

    @Override
    public void doSnapshot(VmwareHypervisorConnection hypervisor, VirtualMachineDefinition vmdefinition,
        DiskStandard destinationDisk) throws VirtualFactoryException
    {
        final PrimaryDisk sourceDisk = vmdefinition.getPrimaryDisk();

        hypervisor.getUtils().getUtilDisks().bundleVirtualDisk(sourceDisk, destinationDisk);
    }

    @Override
    public void reconfigure(VmwareHypervisorConnection hypervisor,
        VirtualMachineDefinition currentvmachine, VirtualMachineDefinition newvmachine)
        throws VirtualFactoryException
    {
        ResourceAllocationInfo raRAM;
        ResourceAllocationInfo raCPU;

        VirtualMachineConfigSpec vmConfigSpec;
        VirtualDeviceConfigSpec[] vdiskSpec = null;

        // TODO utils.reconnect();

        try
        {

            // utils.getVmMor(machineName);
            vmConfigSpec = new VirtualMachineConfigSpec();

            // Setting the new Ram value
            long newRam = ReconfigureUtils.getRamInMbDiff(currentvmachine, newvmachine);
            int newCpu = ReconfigureUtils.getNumVirtualCpuDiff(currentvmachine, newvmachine);
            
            if (newRam != 0)
            {
                Long ram = Long.valueOf(newRam / 1048576);
                vmConfigSpec.setMemoryMB(ram); //
            }

            if (newCpu != 0)
            {
                vmConfigSpec.setNumCPUs(newCpu);                
            }

            // Setting the disk disk value
            // logger.info("Reconfiguring The Virtual Machine For disk Update " + machineName);

            // TODO community doesn't change disks
            // vdiskSpec = disks.getDiskDeviceConfigSpec(newConfiguration);
            //
            // if (vdiskSpec != null)
            // {
            // vmConfigSpec.setDeviceChange(vdiskSpec);
            // }
            // else
            // {
            // logger.debug("Any disk configruation changed");
            // }

            ManagedObjectReference vmMor = hypervisor.getUtils().getVmMor(currentvmachine.getMachineID());
            ManagedObjectReference tmor =
                hypervisor.getUtils().getVimStub().reconfigVM_Task(vmMor, vmConfigSpec);
            hypervisor.getUtils().monitorTask(tmor);
            // Updating configuration

            // vmConfig = newConfiguration;
            // disks.setVMConfig(vmConfig);

            // TODO vmdef = newVmDesc;

        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryError.RECONFIG, String.format(
                "Virtual Machine : %s" + "\nCaused by:%s", currentvmachine.getMachineID(), e.toString()));
        }
        // TODO
        // finally
        // {
        // utils.logout();
        // }

    }

    //
    // /**
    // * Attach the initial extended disk on configuration
    // */
    // private void initDisks(SecondaryDisks disks) throws VirtualFactoryException
    // {
    // // VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
    // // VirtualDeviceConfigSpec[] vdiskSpec;
    // //
    // // try
    // // {
    // // _virtualMachine = utils.getVmMor(machineName);
    // //
    // // vdiskSpec = disks.initialDiskDeviceConfigSpec();
    // //
    // // if (vdiskSpec != null)
    // // {
    // // logger.debug("Adding [{}] initial extended disks", vdiskSpec.length);
    // // vmConfigSpec.setDeviceChange(vdiskSpec);
    // // }
    // // else
    // // {
    // // logger.debug("Any disk configruation to add");
    // // }
    // //
    // // ManagedObjectReference tmor =
    // // utils.getService().reconfigVM_Task(_virtualMachine, vmConfigSpec);
    // //
    // // utils.monitorTask(tmor);
    // // }
    // // catch (Exception e)
    // // {
    // // throw new VirtualMachineException("Can not initialize the extended disks", e);
    // // }
    // }
}
