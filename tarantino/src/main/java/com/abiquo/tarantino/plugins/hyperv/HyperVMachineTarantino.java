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
package com.abiquo.tarantino.plugins.hyperv;

import static com.abiquo.tarantino.plugins.hyperv.utils.HyperVUtils.enumToJIVariantArray;
import static org.jinterop.dcom.impls.JIObjectFactory.narrowObject;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualNIC;
import com.abiquo.tarantino.errors.VirtualFactoryErrors;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.plugins.hyperv.utils.HyperVConstants;
import com.abiquo.tarantino.plugins.hyperv.utils.HyperVUtils;
import com.abiquo.tarantino.plugins.hyperv.utils.MsvmVirtualSwitchManagementServiceExtended;
import com.abiquo.tarantino.plugins.hyperv.utils.MsvmVirtualSystemManagementServiceExtended;
import com.abiquo.tarantino.virtualmachine.IVirtualMachine;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmComputerSystem;

/**
 * @author destevez
 */
public class HyperVMachineTarantino implements IVirtualMachine
{
    private static final Logger logger = LoggerFactory.getLogger(HyperVMachineTarantino.class);

    private VirtualMachineDefinition vmdefinition;

    private HyperVConnection connection;

    /**
     * The virtual machine dispatch
     */
    protected IJIDispatch vmDispatch;

    @Override
    public void doConfigure(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
    {
        this.vmdefinition = vmdefinition;
        this.connection = (HyperVConnection) connection;

        // TODO : Fix Logins!!
//        connection.login(this.connection);

        try
        {
            
            vmDispatch = getVmDispatch(vmdefinition.getMachineID());
            
            configureBasicResources();
            configureVirtualDiskResources();
            configureNetwork();
        }
        catch (Exception e)
        {
            // TODO: Throw exception
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            connection.logout();
        }

    }

    @Override
    public void doDeconfigure(IHypervisorConnection connection,
        VirtualMachineDefinition vmdefinition) throws VirtualFactoryException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void doPause(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        changeVirtualMachineState(HyperVConstants.PAUSED, vmdefinition.getMachineID(), connection);

    }

    @Override
    public void doPowerOff(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        changeVirtualMachineState(HyperVConstants.POWER_OFF, vmdefinition.getMachineID(),
            connection);
    }

    @Override
    public void doPowerOn(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        changeVirtualMachineState(HyperVConstants.POWER_ON, vmdefinition.getMachineID(), connection);
    }

    @Override
    public void doReset(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        changeVirtualMachineState(HyperVConstants.REBOOT, vmdefinition.getMachineID(), connection);

    }

    @Override
    public void doResume(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        changeVirtualMachineState(HyperVConstants.POWER_ON, vmdefinition.getMachineID(), connection);

    }

    @Override
    public void doSnapshot(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition,
        DiskStandard destinationDisk) throws VirtualFactoryException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exist(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition) throws VirtualFactoryException
    {
        boolean exists = false;
        
        try
        {
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, vmdefinition.getMachineID());

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                this.connection.getVirtualizationService().execQuery(query,
                    MsvmComputerSystem.class);
            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();
            if (virtualMachine == null)
            {
                String message =
                    "We couldn't get the state of the virtual machine since it doesn't exist";
                logger.error(message);
                exists =  false;
            } else {
                exists = true;
            }
        }
        catch (Exception e)        
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
        }
        finally
        {
            connection.logout();
        }

        return exists;
    }

    @Override
    public State getState(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition) throws VirtualFactoryException
    {
        State state = State.UNKNOWN;
        try
        {
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, vmdefinition.getMachineID());
            state = null;

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                this.connection.getVirtualizationService().execQuery(query,
                    MsvmComputerSystem.class);
            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();
            if (virtualMachine == null)
            {
                String message =
                    "We couldn't get the state of the virtual machine since it doesn't exist";
                logger.error(message);
                return State.UNDEPLOYED;
            }
            int vmstate = virtualMachine.getEnabledState();
            switch (vmstate)
            {
                case HyperVConstants.POWER_ON:
                    state = State.ON;
                    break;
                case HyperVConstants.POWER_OFF:
                    state = State.OFF;
                    break;
                case HyperVConstants.SUSPENDED:
                    state = State.PAUSED;
                    break;
                case HyperVConstants.PAUSED:
                    state = State.PAUSED;
                    break;
                default:
                    state = State.UNKNOWN;
                    break;
            }
        }
        catch (Exception e)        
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
            return State.UNKNOWN;
        }
        finally
        {
            connection.logout();
        }

        return state;
    }

    @Override
    public void reconfigure(IHypervisorConnection connection,
        VirtualMachineDefinition currentvmachine, VirtualMachineDefinition newvmachine)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Private helper to configure the basic resources, like RAM and CPU
     * 
     * @throws Exception
     */
    private void configureBasicResources() throws Exception
    {
        logger.debug("Configuring Basic resources");
        // Configure memory RAM
        configureMemory(vmdefinition.getHardwareConfiguration().getRamInMb());
        // Configure processor cores
        configureProcessorCount(vmdefinition.getHardwareConfiguration().getNumVirtualCpus());
    }

    /**
     * Private helper to configure the the VM's NIC, connect them to the external network and attach
     * them to the VM
     * 
     * @throws Exception
     */
    private void configureNetwork() throws Exception
    {
        logger.debug("Configuring network");

        SWbemServices service = connection.getVirtualizationService();

        MsvmVirtualSwitchManagementServiceExtended switchService =
            MsvmVirtualSwitchManagementServiceExtended.getManagementService(service);

        // First Find the Msvm_ExternalEthernetPort instance associated to a physical network
        // adapter

        /*
         * String physicalAdapterQuery = "SELECT * FROM msvm_ExternalEthernetPort"; JIVariant[]
         * paQueryResult = execQuery(physicalAdapterQuery); JIVariant[][] paSet =
         * enumToJIVariantArray(paQueryResult); // Attention!! a private agreement is being done,
         * the first physical ethernet port will be // chose for the network connection IJIDispatch
         * paDispatch = getFirstResource(paSet); String vswitchPath =
         * switchService.createSwitch("external network", "n_mola", 1024);
         */
        int portCount = 1;

        for (VirtualNIC vnic : vmdefinition.getNetworkConfiguration().getVirtualNIC())
        {
            String externalSwitchQuery =
                "SELECT * FROM Msvm_VirtualSwitch WHERE ElementName='" + vnic.getVSwitchName()
                    + "'";

            JIVariant[] externalSwitchQueryResult = execQuery(externalSwitchQuery);

            JIVariant[][] externalVswitchSet = enumToJIVariantArray(externalSwitchQueryResult);

            if (externalVswitchSet.length == 0)
            {
                String msg =
                    "External network: " + vnic.getVSwitchName()
                        + " not found. The networking resources couldn't be configured";
                // logger
                // .error(
                // "External network not found. The VM NIC with MAC address: {} will have no connectivity",
                // vnic.getMacAddress());
                throw new VirtualFactoryException(VirtualFactoryErrors.NETWORK_CONFIGURATION, msg);

            }

            IJIDispatch externalVswitchDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(externalVswitchSet[0][0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            // Getting the path of the virtual switch

            IJIDispatch externalVSPathDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(externalVswitchDispatcher.get("Path_")
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            // Getting the virtual machine path
            String virtualSwitchPath = externalVSPathDispatcher.get("Path").getObjectAsString2();

            // Create the private switch ports for the VM NICS
            String firstNicPortName = "PortName" + portCount + vmdefinition.getMachineID(); // FIXME:
            // vmdefinition.getMachineID()
            // ==
            // machinename
            // ???
            String firtshSwitchPortPath =
                switchService.createSwitchPort(virtualSwitchPath, firstNicPortName,
                    firstNicPortName, null);

            // Creating the Ethernet port

            MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
                MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

            // The synthethic Ethernet port is recommended but the guest additions must be
            // installed, so
            // we are using the emulated port

            // String virtualEthernetQuery = "SELECT * FROM Msvm_SyntheticEthernetPortSettingData";
            String virtualEthernetQuery = "SELECT * FROM Msvm_EmulatedEthernetPortSettingData";

            JIVariant[] veQueryResult = execQuery(virtualEthernetQuery);

            JIVariant[][] veSet = enumToJIVariantArray(veQueryResult);

            IJIDispatch veDefaultDispatcher = getResourceAllocationSettingDataDefault(veSet);

            JIVariant[] clonedVEResult = veDefaultDispatcher.callMethodA("Clone_", null);

            IJIDispatch clonedVEDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(clonedVEResult[0].getObjectAsComObject()
                    .queryInterface(IJIDispatch.IID));

            String addressFirst = vnic.getMacAddress();
            clonedVEDispatcher.put("StaticMacAddress", new JIVariant(true));
            clonedVEDispatcher.put("Address", new JIVariant(addressFirst));
            clonedVEDispatcher.put("Connection",
                new JIVariant(new JIArray(new JIString[] {new JIString(firtshSwitchPortPath)})));
            // clonedVEDispatcher
            // .put(
            // "VirtualSystemIdentifiers",
            // new JIVariant(new JIArray(new JIString[] {new
            // JIString(UUID.randomUUID().toString())})));

            virtualSysteManagementServiceExt.addVirtualSystemResourcesVoid(vmDispatch,
                clonedVEDispatcher);

            // Tagging private port switch with VLAN

            String vlanesdexport =
                "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='"
                    + vnic.getVSwitchName() + "_ExternalPort" + "'";

            JIVariant[] vlanesdexportQueryResult = execQuery(vlanesdexport);

            JIVariant[][] vlanesdSetExport = enumToJIVariantArray(vlanesdexportQueryResult);

            if (vlanesdSetExport.length == 0)
            {
                String msg =
                    "External network: " + vnic.getVSwitchName()
                        + " not connected. The networking resources couldn't be configured";
                throw new VirtualFactoryException(VirtualFactoryErrors.NETWORK_CONFIGURATION, msg);

            }

            IJIDispatch vlanesdexportDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(vlanesdSetExport[0][0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            JIArray trunkedList = vlanesdexportDispatch.get("TrunkedVLANList").getObjectAsArray();
            JIVariant[] trunkArray = (JIVariant[]) trunkedList.getArrayInstance();
            JIVariant[] modifiedTrunkArray = new JIVariant[trunkArray.length + 1];
            System.arraycopy(trunkArray, 0, modifiedTrunkArray, 0, trunkArray.length);
            modifiedTrunkArray[trunkArray.length] = new JIVariant(vnic.getVlanTag());
            vlanesdexportDispatch.put("TrunkedVLANList",
                new JIVariant(new JIArray(modifiedTrunkArray)));
            vlanesdexportDispatch.callMethodA("Put_", null);
            // service.getObjectDispatcher().callMethodA("Put", vlanesdexportDispatch.)

            String vlanesd =
                "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='" + firstNicPortName
                    + "'";

            JIVariant[] vlanesdQueryResult = execQuery(vlanesd);

            JIVariant[][] vlanesdSet = enumToJIVariantArray(vlanesdQueryResult);

            IJIDispatch vlanesdDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(vlanesdSet[0][0].getObjectAsComObject()
                    .queryInterface(IJIDispatch.IID));

            vlanesdDispatch.put("AccessVLAN", new JIVariant(vnic.getVlanTag()));
            vlanesdDispatch.callMethodA("Put_", null);

            portCount++;
        }

    }

    /**
     * Private helper to configure the booting virtual disks resources
     * 
     * @throws Exception
     */
    private void configureVirtualDiskResources() throws Exception
    {
        logger.debug("Configuring Virtual disks resources");
        // Getting the IDE controller
        IJIDispatch ideControllerDispatch = getIdeControllerByAddress(0);

        // Getting the dispatcher of the ide resource added Path
        IJIDispatch idePathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(ideControllerDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        String ideControllerPath = idePathDispatcher.get("Path").getObjectAsString2();

        // if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
        // {
        // configureVHDDisk(ideControllerPath, 0);
        // }

        if (vmdefinition.getPrimaryDisk().getDiskStandardConf().getDiskStandard() != null)
        {
            configureVHDDisk(ideControllerPath, 0);
        }
    }

    /**
     * Configures the memory in the VM
     * 
     * @param memoryRam the new memory RAM
     * @throws Exception
     */
    private void configureMemory(final long memoryRam) throws Exception
    {
        IJIDispatch memorySettingDataDispatch = getRASDByClass("Msvm_MemorySettingData");

        memorySettingDataDispatch.put("VirtualQuantity", new JIVariant(memoryRam));
        // memorySettingDataDispatch.put("Reservation", new JIVariant(memoryRam));
        // memorySettingDataDispatch.put("Limit", new JIVariant(100000));

        SWbemServices service = connection.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        virtualSysteManagementServiceExt.modifyVirtualSystemResources(this.vmDispatch,
            memorySettingDataDispatch);

    }

    /**
     * Configures the processor in the VM
     * 
     * @param processorCount the new processor count
     * @throws Exception
     */
    private void configureProcessorCount(final int processorCount) throws Exception
    {
        IJIDispatch processorSettingDataDispatch = getRASDByClass("Msvm_ProcessorSettingData");

        processorSettingDataDispatch
            .put("VirtualQuantity", new JIVariant(new Long(processorCount)));
        // processorSettingDataDispatch.put("Reservation", new JIVariant(new Long(processorCount)));
        // processorSettingDataDispatch.put("Limit", new JIVariant(new Long(processorCount)));
        processorSettingDataDispatch.put("Limit", new JIVariant(100000));

        SWbemServices service = connection.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        virtualSysteManagementServiceExt.modifyVirtualSystemResources(this.vmDispatch,
            processorSettingDataDispatch);
    }

    /**
     * Private helper to execute query with the virtualization service
     * 
     * @param query the query to execute
     * @return array of results
     * @throws JIException
     */
    private JIVariant[] execQuery(final String query) throws JIException
    {
        SWbemServices service = connection.getVirtualizationService();

        IJIDispatch objectDispatcher = service.getObjectDispatcher();

        Object[] inParams =
            new Object[] {new JIString(query), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),};

        JIVariant[] results = objectDispatcher.callMethodA("ExecQuery", inParams);
        return results;
    }

    /**
     * Gets the IDE controller of a resource set by address
     * 
     * @param address the address of the controller to get
     * @param rasdSet the resource allocation setting data set
     * @return the default resource allocation setting data
     * @throws JIException
     */
    protected IJIDispatch getIdeControllerByAddress(final int address) throws JIException
    {
        // Getting the SCSI controller
        String query =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.IDECONTROLLER + "'";

        JIVariant[] ideQueryResult = execQuery(query);

        JIVariant[][] tmpSetIde = enumToJIVariantArray(ideQueryResult);

        IJIDispatch resourceDispatch = null;
        for (JIVariant[] element : tmpSetIde)
        {
            try
            {
                IJIDispatch resource =
                    (IJIDispatch) JIObjectFactory.narrowObject(element[0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                if (String.valueOf(address).equals(resource.get("Address").getObjectAsString2()))
                {
                    resourceDispatch = resource;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + vmdefinition.getMachineID());
            }
        }

        return resourceDispatch;
    }

    /**
     * Configures the virtual hard disk resource to attache the VHD cloned file
     * 
     * @param controllerPath the controller path to use
     * @param addressSlot the address slot to attach the resource in the controller
     * @throws Exception
     */
    protected void configureVHDDisk(final String controllerPath, final int addressSlot)
        throws Exception
    {
        // Getting the disk default drive
        String diskDefaultQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.DISKSYNTHETIC + "'";

        JIVariant[] diskDefaultQueryResult = execQuery(diskDefaultQuery);

        JIVariant[][] tmpSetDiskDefault = enumToJIVariantArray(diskDefaultQueryResult);

        IJIDispatch diskDispatcher = getResourceAllocationSettingDataDefault(tmpSetDiskDefault);

        JIVariant[] cloneDiskDefaultResult = diskDispatcher.callMethodA("Clone_", null);

        IJIDispatch clonedDiskDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(cloneDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        clonedDiskDispatcher.put("Parent", new JIVariant(controllerPath));
        clonedDiskDispatcher.put("Address", new JIVariant(addressSlot));

        SWbemServices service = connection.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        String diskResourcePath =
            virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
                clonedDiskDispatcher);

        String vhdQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.VHD + "'";

        JIVariant[] vhdDefaultQueryResult = execQuery(vhdQuery);

        JIVariant[][] tmpSetVHDDiskDefault = enumToJIVariantArray(vhdDefaultQueryResult);

        IJIDispatch vhdDispatch = getResourceAllocationSettingDataDefault(tmpSetVHDDiskDefault);

        JIVariant[] clonevhdDiskDefaultResult = vhdDispatch.callMethodA("Clone_", null);

        IJIDispatch clonedvhdDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(clonevhdDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        clonedvhdDispatcher.put("Parent", new JIVariant(diskResourcePath));
        
        // FIXME: This probably sucks
        String destinationImagePath = vmdefinition.getPrimaryDisk().getDiskStandardConf().getDestinationDatastore() + vmdefinition.getMachineID();      
        
        
        clonedvhdDispatcher.put("Connection",
            new JIVariant(new JIArray(new JIString[] {new JIString(destinationImagePath)})));

        virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
            clonedvhdDispatcher);
    }

    /**
     * Gets the RASD default of a resource set
     * 
     * @param rasdSet the resource allocation setting data set
     * @return the default resource allocation setting data
     * @throws JIException
     */
    protected IJIDispatch getResourceAllocationSettingDataDefault(final JIVariant[][] rasdSet)
        throws JIException
    {
        IJIDispatch resourceDispatch = null;
        for (JIVariant[] element : rasdSet)
        {
            try
            {
                IJIDispatch resource =
                    (IJIDispatch) JIObjectFactory.narrowObject(element[0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                if (resource.get("InstanceID").getObjectAsString2().contains("Default"))
                {
                    resourceDispatch = resource;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + vmdefinition.getMachineID());
            }
        }

        return resourceDispatch;
    }

    private IJIDispatch getRASDByClass(final String className) throws Exception
    {
        // Getting the virtual machine path
        String vmPath = getDispatchPath(this.vmDispatch);

        JIVariant[] tmp =
            execQuery("Associators of {" + vmPath
                + "} Where ResultClass=Msvm_VirtualSystemSettingData");
        JIVariant[][] vssdVariantArray = enumToJIVariantArray(tmp);
        IJIDispatch vmSettingDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(vssdVariantArray[0][0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        String vmSettingPath = getDispatchPath(vmSettingDispatch);

        JIVariant[] tmp2 =
            execQuery("Associators of {" + vmSettingPath + "} Where ResultClass=" + className
                + " AssocClass=Msvm_VirtualSystemSettingDataComponent "
                + "ResultRole = PartComponent " + "Role = GroupComponent");
        JIVariant[][] resourceSettingVariantArray = enumToJIVariantArray(tmp2);

        return (IJIDispatch) JIObjectFactory.narrowObject(resourceSettingVariantArray[0][0]
            .getObjectAsComObject().queryInterface(IJIDispatch.IID));
    }

    /**
     * Gets the path of the dispatch
     * 
     * @param dispatch the dispatch to get the path from
     * @return the dispatch path
     * @throws JIException
     */
    protected String getDispatchPath(final IJIDispatch dispatch) throws JIException
    {
        // Getting the dispatcher of the Path
        IJIDispatch pathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(dispatch.get("Path_").getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));

        // Getting the path
        return pathDispatcher.get("Path").getObjectAsString2();
    }

    /**
     * Private helper to change the virtual machine state
     * 
     * @param state the state to change
     * @throws VirtualMachineException
     */
    private void changeVirtualMachineState(final int state, String machineName,
        IHypervisorConnection con) throws VirtualFactoryException
    {
        try
        {
            // Preparing the query
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, vmdefinition.getMachineID());

            JIVariant[] queryResult = execQuery(query);

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                connection.getVirtualizationService().execQuery(query, MsvmComputerSystem.class);

            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();
            if (virtualMachine == null)
            {
                String message =
                    "We couldn't get the state of the virtual machine since it doesn't exist";
                logger.error(message);
                throw new VirtualFactoryException(VirtualFactoryErrors.VIRTUAL_MACHINE_NOT_FOUND,
                    message);
            }

            JIVariant[][] machineSetArray = HyperVUtils.enumToJIVariantArray(queryResult);
            IJIDispatch machineDispatch =
                (IJIDispatch) narrowObject(machineSetArray[0][0].getObjectAsComObject());
            Object[] params = new Object[] {new Integer(state), JIVariant.EMPTY_BYREF(), null};
            JIVariant[] res = machineDispatch.callMethodA("RequestStateChange", params);
            int result = res[0].getObjectAsInt();
            if (result == 0)
            {
                logger.debug(machineName + "State changed succesfully");
            }
            else
            {
                if (result == 4096)
                {
                    logger.debug("State changed to " + state + " on...");
                    try
                    {
                        String jobPath = res[1].getObjectAsVariant().getObjectAsString2();
                        HyperVUtils.monitorJob(jobPath, connection.getVirtualizationService()
                            .getObjectDispatcher());
                        logger.debug(machineName + "State changed succesfully");
                    }
                    catch (Exception e)
                    {
                        String message =
                            "An exception occured while monitoring " + machineName
                                + "this state changement" + state + e;
                        throw new VirtualFactoryException(VirtualFactoryErrors.EXECUTING_ACTION,
                            message);
                    }
                }
                else
                {
                    String message =
                        "Failed at powering " + machineName + " on. Error code: " + result;
                    throw new VirtualFactoryException(VirtualFactoryErrors.EXECUTING_ACTION,
                        message);
                }
            }

        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.EXECUTING_ACTION, e.getMessage());
        }
    }
    
    /**
     * Get the virtual machine object with the given virtual machine name.
     * 
     * @param vmName The name of the virtual machine to find.
     * @return The virtual machine object.
     * @throws JIException If the vmDispatch cannot eb retrieved.
     */
    protected IJIDispatch getVmDispatch(String vmName) throws JIException
    {
        JIVariant[] tmp =
            execQuery("Select * From Msvm_ComputerSystem Where ElementName='" + vmName + "'");
        JIVariant[][] tmpSetVM = enumToJIVariantArray(tmp);

        return (IJIDispatch) JIObjectFactory.narrowObject(tmpSetVM[0][0].getObjectAsComObject()
            .queryInterface(IJIDispatch.IID));
    }

}
