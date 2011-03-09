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

package com.abiquo.tarantino.plugins.esxi.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.VirtualNIC;
import com.abiquo.tarantino.errors.VirtualFactoryErrors;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.plugins.esxi.utils.EsxiUtils.EsxiUtilsException;
import com.vmware.vim25.AlreadyExists;
import com.vmware.vim25.ConfigTarget;
import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.GenericVmConfigFault;
import com.vmware.vim25.HostConfigFault;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostNasVolume;
import com.vmware.vim25.HostNasVolumeSpec;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.KeyAnyValue;
import com.vmware.vim25.LicenseManagerLicenseInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NasDatastoreInfo;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VimFault;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualCdromIsoBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualIDEController;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineDatastoreInfo;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineNetworkInfo;
import com.vmware.vim25.VirtualSCSISharing;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostDatastoreSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * There the abiquo specific Esxi usage.
 */
public class VmwareMachineBasics
{
    /** The logger object. */
    private final static Logger logger = LoggerFactory.getLogger(VmwareMachineBasics.class);

    private EsxiUtils utils;

    public VmwareMachineBasics(EsxiUtils utils)
    {
        this.utils = utils;
    }

    protected EsxiUtils getUtils()
    {
        return utils;
    }

    /**
     * Private helper to check if vmware license is not FREE basic
     */
    public void checkLicense() throws VirtualFactoryException
    {
        ManagedObjectReference licenseManager =
            utils.getServiceInstance().getServiceContent().getLicenseManager();

        try
        {
            LicenseManagerLicenseInfo[] licenseInfo =
                (LicenseManagerLicenseInfo[]) utils.getDynamicProperty(licenseManager, "licenses");
            for (LicenseManagerLicenseInfo licenseManagerLicenseInfo : licenseInfo)
            {
                if (licenseManagerLicenseInfo.getEditionKey().equals("esxBasic"))
                {
                    throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_LICENSE,
                        "EsxBasic licese not supported");
                }

                KeyAnyValue[] properties = licenseManagerLicenseInfo.getProperties();

                Long expirationHours = new Long(0);
                Long expirationMinutes = new Long(0);
                boolean neverExpires = true;

                for (int i = 0; i < properties.length; i++)
                {
                    KeyAnyValue keyAnyValue = properties[i];

                    if ("expirationHours".equals(keyAnyValue.getKey()))
                    {
                        expirationHours = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                    else if ("expirationMinutes".equals(keyAnyValue.getKey()))
                    {
                        expirationMinutes = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                }

                if (!neverExpires)
                {
                    if ((expirationHours.intValue() == 0) || (expirationMinutes.intValue() == 0))
                    {
                        throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_LICENSE,
                            "Expired licese");
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_LICENSE,
                "ESXi version not supported");
        }
    }

    /**
     * returns the datastore san name to be used as repository
     */
    public String getAndCheckRepositoryDatastore(String repositoryLocation)
        throws VirtualFactoryException
    {
        String errorMessage =
            String.format("Can not configure the repository datastore [%s]", repositoryLocation);

        int pos = repositoryLocation.indexOf(':');
        String remoteHost = repositoryLocation.substring(0, pos);
        String remotePath = repositoryLocation.substring(pos + 1);

        if (remotePath.endsWith("/"))
        {
            remotePath = remotePath.substring(0, remotePath.length() - 1);
        }

        try
        {
            return obtainNAS(remoteHost, remotePath);
        }
        catch (DuplicateName e)
        {
            logger.warn("Datastore already configured " + repositoryLocation + " \t at :"
                + e.getName());

            // the duplicated name
            return e.getName();
        }
        catch (VimFault ev)
        {

            String errorDetail =
                (ev.getFaultMessage() != null && ev.getFaultMessage().length > 0) ? ev
                    .getFaultMessage()[0].message : ev.getMessage();

            throw new VirtualFactoryException(VirtualFactoryErrors.REPOSITORY_CONFIGURATION,
                errorMessage + "\n" + errorDetail);

        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.REPOSITORY_CONFIGURATION,
                errorMessage);
        }
    }

    /**
     * It prepares the vmware ESXi to work with abicloud infrastructure. It creates a NFS datastore
     * from the configuration file
     * 
     * @param hostName the hostname where the datastore will be created.
     * @throws Exception
     * @return the local name to be used to refere to the NAS datastore
     * @throws VirtualFactoryException
     * @throws HostConfigFault
     * @throws DuplicateName
     * @throws AlreadyExists
     */
    private String obtainNAS(final String remoteHost, final String remotePath)
        throws VirtualFactoryException, HostConfigFault, DuplicateName, AlreadyExists
    {
        HostSystem host = utils.getHostSystem();
        Datastore[] datastores;
        try
        {
            datastores = host.getDatastores();
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION,
                e.getMessage());
        }

        if (datastores == null)
        {
            datastores = new Datastore[0];
        }

        for (Datastore datastore : datastores)
        {
            DatastoreInfo dsinfo = datastore.getInfo();

            // looking only on NAS based datastores
            if (dsinfo instanceof NasDatastoreInfo)
            {
                NasDatastoreInfo nasinfo = (NasDatastoreInfo) dsinfo;
                HostNasVolume nas = nasinfo.getNas();

                if (remoteHost.equalsIgnoreCase(nas.getRemoteHost())
                    && remotePath.equalsIgnoreCase(nas.getRemotePath()))
                {
                    return nas.getName();
                }
            }
        }

        // Datastore not found, creating it.
        final String localPath = String.format("nfsrepository-%s", remoteHost);

        HostNasVolumeSpec spec = new HostNasVolumeSpec();
        spec.setRemoteHost(remoteHost);
        spec.setAccessMode("readWrite");// HostMountMode.readWrite.name());
        spec.setRemotePath(remotePath);
        spec.setLocalPath(localPath);
        // can configure username/password for 'type' CIFS

        HostDatastoreSystem hds;
        try
        {
            hds = host.getHostDatastoreSystem();
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION,
                e.getMessage());
        }
        Datastore newDs;
        try
        {
            newDs = hds.createNasDatastore(spec);
        }
        catch (HostConfigFault e)
        {
            throw e;
        }
        catch (DuplicateName e)
        {
            throw e;
        }
        catch (AlreadyExists e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION,
                e.getMessage());
        }

        // ManagedObjectReference hostDatastoreSystemMOR = hds.getMOR();
        // ManagedObjectReference newdatastore =
        // utils.getService().createNasDatastore(hostDatastoreSystemMOR, spec);

        return newDs.getName();
    }

    // ////////////////// from abs vmware machine
    // ////////////////// from abs vmware machine
    // // ////////////////// from abs vmware machine
    // ////////////////// from abs vmware machine
    // // ////////////////// from abs vmware machine

    // ////////////////// from servicutils / esxiutils
    // ////////////////// from servicutils / esxiutils
    // ////////////////// from servicutils / esxiutils
    // ////////////////// from servicutils / esxiutils
    // ////////////////// from servicutils / esxiutils

    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////

    // controllers

    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param rdmIQN, the IQN of the primary volume, if not null its an statefull image.
     */
    public VirtualMachineConfigSpec createVmConfigSpec(String vmName, String datastoreName,
        long diskSize, ManagedObjectReference computeResMor, ManagedObjectReference hostMor,
        String rdmIQN)// , VmwareMachineDisk disks)
        throws VirtualFactoryException
    {
        int diskCtlrKey = 1; // TODO ????

        ConfigTarget configTarget = utils.getConfigTargetForHost(computeResMor, hostMor);

        // String networkName = getNetworkName(configTarget);

        ManagedObjectReference datastoreRef = getDatastore(configTarget, datastoreName);
        String datastoreVolume = getVolumeName(datastoreName);

        VirtualMachineFileInfo vmfi = new VirtualMachineFileInfo();
        vmfi.setVmPathName(datastoreVolume);

        VirtualDevice ideCtlr = getIDEController(computeResMor, hostMor);
        VirtualDeviceConfigSpec cdSpec =
            createCDDevice(ideCtlr.getKey(), datastoreRef, datastoreVolume);
        VirtualDeviceConfigSpec scsiSpec = createSCSIController(diskCtlrKey);

        // Create a new disk - file based - for the vm
        VirtualDeviceConfigSpec diskSpec = null;

        // stateless
        if (rdmIQN == null)
        {
            diskSpec =
                utils.createVirtualDisk(datastoreVolume, diskCtlrKey, datastoreRef, diskSize);
        }

        // Add a NIC. the network Name must be set as the device name to create the NIC.//AQUI!!!

        List<VirtualDeviceConfigSpec> configSpecList = new LinkedList<VirtualDeviceConfigSpec>();
        configSpecList.add(scsiSpec);
        // configSpecList.add(floppySpec);
        if (diskSpec != null)
        {
            configSpecList.add(diskSpec);
        }
        if (ideCtlr != null)
        {
            configSpecList.add(cdSpec);
        }

        // List<VirtualDeviceConfigSpec> nicSpecList = configureNetworkInterfaces(vnicList);
        // configSpecList.addAll(nicSpecList);

        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        configSpec.setFiles(vmfi);
        configSpec.setDeviceChange(configSpecList.toArray(new VirtualDeviceConfigSpec[] {}));

        return configSpec;
    }

    //
    // if (ideCtlr != null)
    // {
    // deviceConfigSpec = new VirtualDeviceConfigSpec[6]; // XXX[6]
    //
    // deviceConfigSpec[3] = cdSpec;
    // deviceConfigSpec[4] = nicSpecPrivate;
    // if (assignPublicInterface)
    // {
    // deviceConfigSpec[5] = nicSpecPublic;
    // }
    //
    // }
    // else
    // {
    // deviceConfigSpec = new VirtualDeviceConfigSpec[5];
    // deviceConfigSpec[3] = nicSpecPrivate;
    // if (assignPublicInterface)
    // {
    // deviceConfigSpec[4] = nicSpecPublic;
    // }
    // }
    //
    // deviceConfigSpec[0] = scsiCtrlSpec;
    // deviceConfigSpec[1] = floppySpec;
    // deviceConfigSpec[2] = diskSpec;

    // /////////////////////////////////////
    // XXX deviceConfigSpec[5] = diskSpec2;
    // /////////////////////////////////////

    private ManagedObjectReference getDatastore(ConfigTarget configTarget,
        final String datastoreName) throws VirtualFactoryException
    {

        if (datastoreName != null)
        {
            for (int i = 0; i < configTarget.getDatastore().length; i++)
            {
                VirtualMachineDatastoreInfo vdsInfo = configTarget.getDatastore()[i];
                DatastoreSummary dsSummary = vdsInfo.getDatastore();
                if (dsSummary.getName().equals(datastoreName))
                {
                    if (dsSummary.isAccessible())
                    {
                        return dsSummary.getDatastore();
                    }
                    else
                    {
                        throw new VirtualFactoryException(VirtualFactoryErrors.DATASTORE_NOT_ACCESSIBLE,
                            datastoreName);
                    }
                }
            }

            throw new VirtualFactoryException(VirtualFactoryErrors.DATASTORE_NOT_FOUND,
                datastoreName);
        }
        else
        {
            // TODO can be possible ???
            logger
                .warn(" !!!!!!!!! Datastore not provided, get the first accessible datastore. !!!!!!!!!!!!");
            for (int i = 0; i < configTarget.getDatastore().length; i++)
            {
                VirtualMachineDatastoreInfo vdsInfo = configTarget.getDatastore()[i];
                DatastoreSummary dsSummary = vdsInfo.getDatastore();
                if (dsSummary.isAccessible())
                {
                    logger.warn("As you don't provide a datastore i will use "
                        + dsSummary.getName());
                    return dsSummary.getDatastore();
                }
            }

            throw new VirtualFactoryException(VirtualFactoryErrors.DATASTORE_NOT_FOUND,
                "Any datastore is accessible");
        }
    }

    private String getVolumeName(String volName)
    {
        String volumeName = null;
        if (volName != null && volName.length() > 0)
        {
            volumeName = "[" + volName + "]";
        }
        else
        {
            volumeName = "[Local]";
        }

        return volumeName;
    }

    private VirtualDeviceConfigSpec createSCSIController(int diskCtlrKey)
    {
        // Add a scsi controller TODO

        VirtualLsiLogicController scsiCtrl = new VirtualLsiLogicController();
        scsiCtrl.setBusNumber(0);
        scsiCtrl.setSharedBus(VirtualSCSISharing.noSharing); // XXX EBS
        scsiCtrl.setKey(diskCtlrKey);
        // XXX scsiCtrl.setConnectable();
        // XXX scsiCtrl.setSharedBus(VirtualSCSISharing.physicalSharing);

        // String ctlrType = scsiCtrl.getClass().getName();
        // ctlrType = ctlrType.substring(ctlrType.lastIndexOf(".") + 1);

        VirtualDeviceConfigSpec scsiCtrlSpec = new VirtualDeviceConfigSpec();
        scsiCtrlSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
        scsiCtrlSpec.setDevice(scsiCtrl);

        return scsiCtrlSpec;

        // Add a scsi controller
        /*
         * int diskCtlrKey2 = 2; VirtualDeviceConfigSpec scsiCtrlSpec2 = new
         * VirtualDeviceConfigSpec();
         * scsiCtrlSpec2.setOperation(VirtualDeviceConfigSpecOperation.add);
         * VirtualLsiLogicController scsiCtrl2 = new VirtualLsiLogicController();
         * scsiCtrl2.setBusNumber(1); scsiCtrlSpec2.setDevice(scsiCtrl2);
         * scsiCtrl2.setKey(diskCtlrKey2);
         * scsiCtrl2.setSharedBus(VirtualSCSISharing.physicalSharing); String ctlrType2 =
         * scsiCtrl2.getClass().getName(); ctlrType2 =
         * ctlrType2.substring(ctlrType2.lastIndexOf(".") + 1);
         */
    }

    private VirtualDevice getIDEController(ManagedObjectReference computeResMor,
        ManagedObjectReference hostMor) throws VirtualFactoryException
    {
        VirtualDevice[] defaultDevices = utils.getDefaultDevices(computeResMor, hostMor);

        // Find the IDE controller
        VirtualDevice ideCtlr = null;
        for (int di = 0; di < defaultDevices.length; di++)
        {
            if (defaultDevices[di] instanceof VirtualIDEController)
            {
                ideCtlr = defaultDevices[di];
                break;
            }
        }

        if (ideCtlr == null)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.VIRTUAL_MACHINE_RETRIEVE_ERROR,
                "Can't get the IDE controller"); // TODO vf
        }

        return ideCtlr;
    }

    private VirtualDeviceConfigSpec createCDDevice(Integer IdeCtlrKey,
        ManagedObjectReference datastoreRef, String datastoreVolume)
    {
        // Add a cdrom based on a physical device

        VirtualCdromIsoBackingInfo cdDeviceBacking = new VirtualCdromIsoBackingInfo();
        cdDeviceBacking.setDatastore(datastoreRef);
        cdDeviceBacking.setFileName(datastoreVolume + "testcd.iso");

        VirtualCdrom cdrom = new VirtualCdrom();
        cdrom.setControllerKey(IdeCtlrKey);
        cdrom.setKey(20);
        cdrom.setBacking(cdDeviceBacking);
        cdrom.setUnitNumber(new Integer(0));

        VirtualDeviceConfigSpec cdSpec = null;
        cdSpec = new VirtualDeviceConfigSpec();
        cdSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
        cdSpec.setDevice(cdrom);

        return cdSpec;
    }

    // // Add a floppy
    // VirtualDeviceConfigSpec floppySpec = new VirtualDeviceConfigSpec();
    // floppySpec.setOperation(VirtualDeviceConfigSpecOperation.add);
    // VirtualFloppy floppy = new VirtualFloppy();
    // VirtualFloppyDeviceBackingInfo flpBacking = new VirtualFloppyDeviceBackingInfo();
    // flpBacking.setDeviceName("/dev/fd0");
    // floppy.setBacking(flpBacking);
    // floppy.setKey(3);
    // floppySpec.setDevice(floppy);

    /**
     * // * Open a session to the VMWare ESXi // * // * @throws VirtualMachineException // * @throws
     * HypervisorException //
     */
    // public void reconnect() throws VirtualMachineException
    // {
    // URL address = null;
    // try
    // {
    // address = vmwareHyper.getAddress();
    //
    // vmwareHyper.init(address);
    // vmwareHyper.connect(address);
    // apputil = vmwareHyper.getAppUtil();
    // }
    // catch (HypervisorException e)
    // {
    // logger.error("An error was occurred when reconnecting to the hypervisor: {}",
    // address.toExternalForm());
    // throw new VirtualMachineException(e);
    //
    // }
    // }

    /** Tasks to be required for a VM to change its state. */
    public enum VMTasks
    {
        PAUSE, POWER_OFF, POWER_ON, RESET, RESUME, DELETE
    };

    /**
     * Call the API service to achieve the desired Task on the current VM.
     */
    public void executeTask(final VMTasks task, VirtualMachine vm) throws VirtualFactoryException
    {
        // TODO utils.reconnect();

        Task taskMOR;

        try
        {
            switch (task)
            {
                case POWER_OFF:
                    taskMOR = vm.powerOffVM_Task();
                    break;
                case POWER_ON:
                    taskMOR = vm.powerOnVM_Task(null);
                    break;
                case PAUSE:
                    taskMOR = vm.suspendVM_Task();
                    break;
                case RESUME:
                    taskMOR = vm.powerOnVM_Task(null);
                    break;
                case RESET:
                    taskMOR = vm.resetVM_Task();
                    break;
                case DELETE:
                    taskMOR = vm.destroy_Task();
                    break;
                default:
                    throw new Exception("Invalid task action " + task.name());
            }

            // TODO it was using wait for me !!!! --->> it take default arguments !!!
            String taskResult = taskMOR.waitForTask();

            if (taskResult.equalsIgnoreCase(TaskInfoState.success.toString()))
            {
                logger.info("[" + task.name() + "] successfuly for VM [{}]", vm.getName());
            }
            else
            {

                final String descr =
                    String.format("VM: %s executing task: %s result on : %s", vm.getName(),
                        task.name(), taskResult);

                /**
                 * TODO task info can obtain
                 * <p/>
                 * TODO taskMOR.getTaskInfo().getError()
                 * <p/>
                 * TODO taskMOR.getTaskInfo().getDescription()
                 * <p/>
                 * TODO progress and reason
                 */

                throw new VirtualFactoryException(VirtualFactoryErrors.EXECUTING_ACTION, descr);
            }
        }
        catch (Exception e)
        {
            String descr =
                String.format("VM: %s executing task: %s fail", vm.getName(), task.name());

            if (e instanceof GenericVmConfigFault)
            {
                GenericVmConfigFault configFault = (GenericVmConfigFault) e;
                descr = descr + "Raison : " + configFault.getReason();
            }
            else
            {
                descr = descr + e.toString();
            }

            throw new VirtualFactoryException(VirtualFactoryErrors.EXECUTING_ACTION, descr);
        }

        // TODO finally
        // {
        // utils.logout();
        // }

    }
}
