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

import static com.vmware.vim25.mo.util.PropertyCollectorUtil.creatObjectSpec;
import static com.vmware.vim25.mo.util.PropertyCollectorUtil.createPropertySpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.abiquo.tarantino.plugins.esxi.VirtualMachineException;
import com.abiquo.tarantino.plugins.esxi.AbsVmwareMachine.VMTasks;
import com.vmware.vim25.ConfigTarget;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.GenericVmConfigFault;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.LocalizedMethodFault;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MethodFault;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.ObjectUpdateKind;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.SharesInfo;
import com.vmware.vim25.SharesLevel;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualMachineConfigOption;
import com.vmware.vim25.VirtualMachineNetworkInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.OptionSpec;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

public class EsxiUtils extends EsxiVim25Util
{

    class EsxiUtilsException extends RuntimeException
    {
        private static final long serialVersionUID = 7679598393436752930L;

        public EsxiUtilsException(final String cause)
        {
            super(cause);
        }

        public EsxiUtilsException(final IOException ex)
        {
            super("Remote communication exception.", ex);
        }

        public EsxiUtilsException(final MethodFault mf)
        {
            super("ESXi invocation fail.", mf);
        }

        public EsxiUtilsException(final Exception e)
        {
            super(e);
        }

        public EsxiUtilsException(final String msg, final Exception e)
        {
            super(msg, e);
        }
    }

    public EsxiUtils(ServiceInstance serviceInstance, OptionSpec[] options,
        Map<String, String> optsEntered)
    {
        super(serviceInstance, options, optsEntered);
    }

    /**
     * return null if any
     */
    public ArrayList getVMs(String entity, String datacenter, String folder, String pool,
        String vmname, String host, String[][] filter) throws EsxiUtilsException
    {
        ManagedObjectReference dsMOR = null;
        ManagedObjectReference hostMOR = null;
        ManagedObjectReference poolMOR = null;
        ManagedObjectReference vmMOR = null;
        ManagedObjectReference folderMOR = null;
        ManagedObjectReference tempMOR = null;
        ArrayList vmList = new ArrayList();
        String[][] filterData = null;

        if (datacenter != null)
        {
            try
            {
                dsMOR = getDecendentMoRef(null, "Datacenter", datacenter);

                if (dsMOR == null)
                {
                    throw new EsxiUtilsException(String.format("Datacenter %s not found",
                        datacenter));
                }
            }
            catch (Exception e)
            {
                throw new EsxiUtilsException(String.format("Datacenter %s not found", datacenter),
                    e);
            }

            tempMOR = dsMOR;
        }
        if (folder != null)
        {
            try
            {
                folderMOR = getDecendentMoRef(tempMOR, "Folder", folder);
                if (folderMOR == null)
                {
                    throw new EsxiUtilsException(String.format("Folder %s not found", folder));
                }
            }
            catch (Exception e)
            {
                throw new EsxiUtilsException(String.format("Folder %s not found", folder), e);
            }

            tempMOR = folderMOR;

        }
        if (pool != null)
        {
            try
            {
                poolMOR = getDecendentMoRef(tempMOR, "ResourcePool", pool);
                if (poolMOR == null)
                {
                    throw new EsxiUtilsException(String.format("ResourcePool %s not found", pool));
                }
            }
            catch (Exception e)
            {
                throw new EsxiUtilsException(String.format("ResourcePool %s not found", pool));
            }

            tempMOR = poolMOR;

        }
        if (host != null)
        {
            try
            {
                hostMOR = getDecendentMoRef(tempMOR, "HostSystem", host);
                if (hostMOR == null)
                {
                    throw new EsxiUtilsException(String.format("Host System %s not found", host));
                }
            }
            catch (Exception e)
            {
                throw new EsxiUtilsException(String.format("Host System %s not found", host));
            }

            tempMOR = hostMOR;
        }

        if (vmname != null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
            // Adding the vmname in the filter
            filterData[i][0] = "name";
            filterData[i][1] = vmname;
        }
        else if (vmname == null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
        }
        vmList = getDecendentMoRefs(tempMOR, "VirtualMachine", filterData);
        if ((vmList == null) || (vmList.size() == 0))
        {
            logger.warn("There aren't any match virtual machine (return null)");
            return null;
        }
        return vmList;

    }

    /**
     * This method returns the contents of the hostFolder property from the supplied Datacenter
     * MoRef
     * 
     * @param dcmor MoRef to the Datacenter
     * @return MoRef to a Folder returned by the hostFolder property or null if dcmor is NOT a MoRef
     *         to a Datacenter or if the hostFolder doesn't exist
     * @throws EsxiUtilsException
     */
    public ManagedObjectReference getHostFolder(ManagedObjectReference dcmor)
        throws EsxiUtilsException
    {
        ManagedObjectReference hfmor = getMoRefProp(dcmor, "hostFolder");
        return hfmor;
    }

    /**
     * This method returns a MoRef to the HostSystem with the supplied name under the supplied
     * Folder. If hostname is null, it returns the first HostSystem found under the supplied Folder
     * 
     * @param hostFolderMor MoRef to the Folder to look in
     * @param hostname Name of the HostSystem you are looking for
     * @return MoRef to the HostSystem or null if not found
     * @throws EsxiUtilsException
     */
    public ManagedObjectReference getHost(ManagedObjectReference hostFolderMor, String hostname)
        throws EsxiUtilsException
    {
        ManagedObjectReference hostmor = null;

        if (hostname != null)
        {
            hostmor = getDecendentMoRef(hostFolderMor, "HostSystem", hostname);
        }
        else
        {
            hostmor = getFirstDecendentMoRef(hostFolderMor, "HostSystem");
        }

        return hostmor;
    }

    /**
     * This method returns the ConfigTarget for a HostSystem
     * 
     * @param computeResMor A MoRef to the ComputeResource used by the HostSystem
     * @param hostMor A MoRef to the HostSystem
     * @return Instance of ConfigTarget for the supplied HostSystem/ComputeResource
     * @throws EsxiUtilsException When no ConfigTarget can be found
     */
    public ConfigTarget getConfigTargetForHost(ManagedObjectReference computeResMor,
        ManagedObjectReference hostMor) throws EsxiUtilsException
    {
        ManagedObjectReference envBrowseMor = getMoRefProp(computeResMor, "environmentBrowser");

        ConfigTarget configTarget = null;
        try
        {
            configTarget =
                getServiceInstance().getServerConnection().getVimService()
                    .queryConfigTarget(envBrowseMor, hostMor);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        if (configTarget == null)
        {
            throw new EsxiUtilsException("No ConfigTarget found in ComputeResource");
        }

        return configTarget;
    }

    /**
     * The method returns the default devices from the HostSystem
     * 
     * @param computeResMor A MoRef to the ComputeResource used by the HostSystem
     * @param hostMor A MoRef to the HostSystem
     * @return Array of VirtualDevice containing the default devices for the HostSystem
     * @throws EsxiUtilsException
     */
    public VirtualDevice[] getDefaultDevices(ManagedObjectReference computeResMor,
        ManagedObjectReference hostMor) throws EsxiUtilsException
    {
        ManagedObjectReference envBrowseMor = getMoRefProp(computeResMor, "environmentBrowser");

        VirtualMachineConfigOption cfgOpt;
        try
        {
            cfgOpt =
                getServiceInstance().getServerConnection().getVimService()
                    .queryConfigOption(envBrowseMor, null, hostMor);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        VirtualDevice[] defaultDevs = null;

        if (cfgOpt == null)
        {
            throw new EsxiUtilsException("No VirtualHardwareInfo found in ComputeResource");
        }
        else
        {
            defaultDevs = cfgOpt.getDefaultDevice();
            if (defaultDevs == null)
            {
                throw new EsxiUtilsException("No Datastore found in ComputeResource");
            }
        }

        return defaultDevs;
    }

    /**
     * XXX deviceName = "/vmfs/devices/disks/vmhba34:5:0:0" lunUuid =
     * "0200000000600144f04a324a7900000c296262e000534f4c415249" public VirtualDeviceConfigSpec
     * createRawDeviceMapping(String volName, int diskCtlrKey, ManagedObjectReference datastoreRef,
     * String deviceName, String lunUuid, long diskSize) { System.err.println("CREATE RDM using");
     * System.err.println("volname "+volName); System.err.println("diskCtrKey "+diskCtlrKey); String
     * volumeName = getVolumeName(volName); VirtualDeviceConfigSpec diskSpec = new
     * VirtualDeviceConfigSpec();
     * diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
     * diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add); VirtualDisk disk = new
     * VirtualDisk(); VirtualDiskRawDiskMappingVer1BackingInfo diskfileBacking = new
     * VirtualDiskRawDiskMappingVer1BackingInfo(); diskfileBacking.setDatastore(datastoreRef);
     * diskfileBacking.setDeviceName(deviceName); diskfileBacking.setLunUuid(lunUuid);
     * diskfileBacking.setCompatibilityMode(VirtualDiskCompatibilityMode._physicalMode);
     * diskfileBacking.setDiskMode(VirtualDiskMode._independent_persistent);
     * diskfileBacking.setFileName(volumeName); disk.setKey(new Integer(0));
     * disk.setControllerKey(new Integer(diskCtlrKey)); disk.setUnitNumber(new Integer(1));
     * disk.setBacking(diskfileBacking); disk.setCapacityInKB(diskSize / 1024);
     * diskSpec.setDevice(disk); return diskSpec; }
     */

    public VirtualDeviceConfigSpec createVirtualDisk(String volumeName, int diskCtlrKey,
        ManagedObjectReference datastoreRef, long diskSize)
    {

        VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();

        diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
        diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add);

        VirtualDisk disk = new VirtualDisk();

        VirtualDiskFlatVer2BackingInfo diskfileBacking = new VirtualDiskFlatVer2BackingInfo();
        diskfileBacking.setFileName(volumeName);
        diskfileBacking.setDiskMode("persistent");

        // System.err.println("it is using volumename "+volumeName);
        // System.err.println("diskCtrlKey "+diskCtlrKey);

        disk.setKey(new Integer(0));
        disk.setControllerKey(new Integer(diskCtlrKey));
        disk.setUnitNumber(new Integer(0));

        disk.setBacking(diskfileBacking);
        disk.setCapacityInKB(diskSize / 1024);

        diskSpec.setDevice(disk);

        return diskSpec;
    } // //////////////////////////////

    // XXX VirtualDeviceConfigSpec diskSpec2 = null;
    /*
     * diskSpec2 = createVirtualDisk_1("[datastore1] VMWareTest/VMWareTest_1.vmdk", diskCtlrKey,
     * datastoreRef, diskSize);
     */
    /*
     * XXX diskSpec2 = createRawDeviceMapping(datastoreName, diskCtlrKey, datastoreRef,
     * "/vmfs/devices/disks/vmhba34:5:0:0",
     * "0200000000600144f04a324a7900000c296262e000534f4c415249", Long.parseLong("3221225472"));
     */

    // //////////////////////////////////////////7
    // // Servuce Util

    static String[] meTree = {"ManagedEntity", "ComputeResource", "ClusterComputeResource",
    "Datacenter", "Folder", "HostSystem", "ResourcePool", "VirtualMachine"};

    static String[] crTree = {"ComputeResource", "ClusterComputeResource"};

    static String[] hcTree = {"HistoryCollector", "EventHistoryCollector", "TaskHistoryCollector"};

    boolean typeIsA(String searchType, String foundType)
    {
        if (searchType.equals(foundType))
        {
            return true;
        }
        else if (searchType.equals("ManagedEntity"))
        {
            for (int i = 0; i < meTree.length; ++i)
            {
                if (meTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        else if (searchType.equals("ComputeResource"))
        {
            for (int i = 0; i < crTree.length; ++i)
            {
                if (crTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        else if (searchType.equals("HistoryCollector"))
        {
            for (int i = 0; i < hcTree.length; ++i)
            {
                if (hcTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the ManagedObjectReference for an item under the specified root folder that has the type
     * and name specified.
     * 
     * @param root a root folder if available, or null for default
     * @param type type of the managed object
     * @param name name to match
     * @return First ManagedObjectReference of the type / name pair found
     */
    public ManagedObjectReference getDecendentMoRef(ManagedObjectReference root, String type,
        String name) throws EsxiUtilsException
    {
        if (name == null || name.length() == 0)
        {
            return null;
        }

        String[][] typeinfo = new String[][] {new String[] {type, "name",},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, true);

        if (ocary == null || ocary.length == 0)
        {
            return null;
        }

        ObjectContent oc = null;
        ManagedObjectReference mor = null;
        DynamicProperty[] propary = null;
        String propval = null;
        boolean found = false;
        for (int oci = 0; oci < ocary.length && !found; oci++)
        {
            oc = ocary[oci];
            mor = oc.getObj();
            propary = oc.getPropSet();

            propval = null;
            if (type == null || typeIsA(type, mor.getType()))
            {
                if (propary.length > 0)
                {
                    propval = (String) propary[0].getVal();
                }

                found = propval != null && name.equals(propval);
            }
        }

        if (!found)
        {
            mor = null;
        }

        return mor;
    }

    /**
     * Get the first ManagedObjectReference from a root of the specified type
     * 
     * @param root a root folder if available, or null for default
     * @param type the type of the entity - e.g. VirtualMachine
     * @return managed object reference available
     */
    public ManagedObjectReference getFirstDecendentMoRef(ManagedObjectReference root, String type)
        throws EsxiUtilsException
    {
        ArrayList morlist = getDecendentMoRefs(root, type);

        ManagedObjectReference mor = null;

        if (morlist.size() > 0)
        {
            mor = (ManagedObjectReference) morlist.get(0);
        }

        return mor;
    }

    /**
     * Retrieve all the ManagedObjectReferences of the type specified.
     * 
     * @param root a root folder if available, or null for default
     * @param type type of container refs to retrieve
     * @return List of MORefs
     */
    public ArrayList getDecendentMoRefs(ManagedObjectReference root, String type)
        throws EsxiUtilsException
    {
        ArrayList mors = getDecendentMoRefs(root, type, null);
        return mors;
    }

    public ArrayList getDecendentMoRefs(ManagedObjectReference root, String type, String[][] filter)
        throws EsxiUtilsException
    {
        String[][] typeinfo = new String[][] {new String[] {type, "name"},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, true);

        ArrayList refs = new ArrayList();

        if (ocary == null || ocary.length == 0)
        {
            return refs;
        }

        for (int oci = 0; oci < ocary.length; oci++)
        {
            refs.add(ocary[oci].getObj());
        }

        if (filter != null)
        {
            ArrayList filtermors = filterMOR(refs, filter);
            return filtermors;
        }
        else
        {
            return refs;
        }
    }

    private ArrayList filterMOR(ArrayList mors, String[][] filter) throws EsxiUtilsException
    {
        ArrayList filteredmors = new ArrayList();
        for (int i = 0; i < mors.size(); i++)
        {
            boolean flag = true;
            String guest = null;
            for (int k = 0; k < filter.length; k++)
            {
                String prop = filter[k][0];
                String reqVal = filter[k][1];
                String value = getProp(((ManagedObjectReference) mors.get(i)), prop);
                if (reqVal == null)
                {
                    continue;

                }

                if (value == null && reqVal == null)
                {
                    continue;

                }

                if (value == null && reqVal != null)
                {
                    flag = false;
                    k = filter.length + 1;

                }
                else if (value.equalsIgnoreCase(reqVal))
                {
                }
                else
                {
                    flag = false;
                    k = filter.length + 1;
                }
            }
            if (flag)
            {
                filteredmors.add(mors.get(i));
            }
        }
        return filteredmors;
    }

    private String getProp(ManagedObjectReference obj, String prop)
    {
        String propVal = null;
        try
        {
            propVal = (String) getDynamicProperty(obj, prop);
        }
        catch (Exception e)
        {
        }
        return propVal;
    }

    /**
     * Retrieve Container contents for all containers recursively from root
     * 
     * @return retrieved object contents
     */
    public ObjectContent[] getAllContainerContents() throws EsxiUtilsException
    {
        ObjectContent[] ocary = getContentsRecursively(null, true);

        return ocary;
    }

    /**
     * Retrieve container contents from specified root recursively if requested.
     * 
     * @param root a root folder if available, or null for default
     * @param recurse retrieve contents recursively from the root down
     * @return retrieved object contents
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference root, boolean recurse)
        throws EsxiUtilsException
    {

        String[][] typeinfo = new String[][] {new String[] {"ManagedEntity",},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, recurse);

        return ocary;
    }

    /**
     * TODO root :: never null -- > if null set to useroot =
     * getServiceInstance().getServiceContent().getRootFolder(); Retrieve content recursively with
     * multiple properties. the typeinfo array contains typename + properties to retrieve.
     * 
     * @param collector a property collector if available or null for default
     * @param root a root folder if available, or null for default
     * @param typeinfo 2D array of properties for each typename
     * @param recurse retrieve contents recursively from the root down
     * @return retrieved object contents
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference collector,
        ManagedObjectReference root, String[][] typeinfo, boolean recurse)
        throws EsxiUtilsException
    {
        if (typeinfo == null || typeinfo.length == 0)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = getPropertyCollector();
        }

        ManagedObjectReference useroot = root;
        if (useroot == null)
        {
            useroot = getServiceInstance().getServiceContent().getRootFolder();
        }

        SelectionSpec[] selectionSpecs = null;
        if (recurse)
        {
            selectionSpecs = PropertyCollectorUtil.buildFullTraversal();
        }

        PropertySpec[] propspecary = buildPropertySpecArray(typeinfo);

        PropertyFilterSpec filterSpec = new PropertyFilterSpec();
        filterSpec.setPropSet(propspecary);
        filterSpec.setObjectSet(new ObjectSpec[] {PropertyCollectorUtil.creatObjectSpec(useroot,
            false, selectionSpecs)});

        ObjectContent[] retoc;
        try
        {
            retoc =
                getServiceInstance().getServerConnection().getVimService()
                    .retrieveProperties(usecoll, new PropertyFilterSpec[] {filterSpec});
        }
        catch (InvalidProperty e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        return retoc;
    }

    /**
     * Get a MORef from the property returned.
     * 
     * @param objMor Object to get a reference property from
     * @param propName name of the property that is the MORef
     * @return ManagedObjectReference.
     */
    public ManagedObjectReference getMoRefProp(ManagedObjectReference objMor, String propName)
        throws EsxiUtilsException
    {
        Object props = getDynamicProperty(objMor, propName);
        ManagedObjectReference propmor = null;
        if (!props.getClass().isArray())
        {
            propmor = (ManagedObjectReference) props;
        }

        return propmor;
    }

    /**
     * Retrieve contents for a single object based on the property collector registered with the
     * service.
     * 
     * @param collector Property collector registered with service
     * @param mobj Managed Object Reference to get contents for
     * @param properties names of properties of object to retrieve
     * @return retrieved object contents
     */
    public ObjectContent[] getObjectProperties(ManagedObjectReference collector,
        ManagedObjectReference mobj, String[] properties) throws EsxiUtilsException
    {
        if (mobj == null)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = getPropertyCollector();
        }

        PropertySpec propertySpec =
            createPropertySpec(mobj.getType(), new Boolean(properties == null
                || properties.length == 0), properties);

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.setPropSet(new PropertySpec[] {propertySpec});

        ObjectSpec objectSpec = creatObjectSpec(mobj, false, new SelectionSpec[] {});
        spec.setObjectSet(new ObjectSpec[] {objectSpec});

        try
        {
            return getServiceInstance().getServerConnection().getVimService()
                .retrieveProperties(usecoll, new PropertyFilterSpec[] {spec});
        }
        catch (InvalidProperty e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }
    }

    /**
     * Retrieve a single object
     * 
     * @param mor Managed Object Reference to get contents for
     * @param propertyName of the object to retrieve
     * @return retrieved object
     */
    public Object getDynamicProperty(ManagedObjectReference mor, String propertyName)
        throws EsxiUtilsException
    {
        ObjectContent[] objContent = getObjectProperties(null, mor, new String[] {propertyName});

        Object propertyValue = null;
        if (objContent != null)
        {
            DynamicProperty[] dynamicProperty = objContent[0].getPropSet();
            if (dynamicProperty != null)
            {
                /*
                 * Check the dynamic propery for ArrayOfXXX object
                 */
                Object dynamicPropertyVal = dynamicProperty[0].getVal();
                String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
                if (dynamicPropertyName.indexOf("ArrayOf") != -1)
                {
                    String methodName =
                        dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf")
                            + "ArrayOf".length(), dynamicPropertyName.length());
                    /*
                     * If object is ArrayOfXXX object, then get the XXX[] by invoking getXXX() on
                     * the object. For Ex: ArrayOfManagedObjectReference.getManagedObjectReference()
                     * returns ManagedObjectReference[] array.
                     */
                    if (methodExists(dynamicPropertyVal, "get" + methodName, null))
                    {
                        methodName = "get" + methodName;
                    }
                    else
                    {
                        /*
                         * Construct methodName for ArrayOf primitive types Ex: For ArrayOfInt,
                         * methodName is get_int
                         */
                        methodName = "get_" + methodName.toLowerCase();
                    }

                    Method getMorMethod;

                    try
                    {
                        getMorMethod =
                            dynamicPropertyVal.getClass().getDeclaredMethod(methodName,
                                (Class[]) null);
                        propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
                    }
                    catch (Exception e)
                    {
                        throw new EsxiUtilsException(e);
                    }
                }
                else if (dynamicPropertyVal.getClass().isArray())
                {
                    /*
                     * Handle the case of an unwrapped array being deserialized.
                     */
                    propertyValue = dynamicPropertyVal;
                }
                else
                {
                    propertyValue = dynamicPropertyVal;
                }
            }
        }
        return propertyValue;
    }

    //

    /**
     * Private helper to check if the task has been success executed.
     * 
     * @param taskmor the MOR to get the info from
     * @throws VirtualMachineException it there is any error.
     */
    public void checkTaskState(ManagedObjectReference taskmor) throws EsxiUtilsException
    {
        DynamicProperty[] taskInfoProperty;
        TaskInfo tinfo;
        String taskResult;

        try
        {
            taskInfoProperty = getDynamicProarray(taskmor, "info");
            tinfo = ((TaskInfo) (taskInfoProperty[0]).getVal()); // TODO only the first relevant ??
        }
        catch (Exception e)
        {
            final String msg =
                "Can not get the Dynamic property ''info'' for task " + taskmor.get_value();
            throw new EsxiUtilsException(msg, e);
        }

        // wait task completion

        try
        {
            taskResult = waitForTask(taskmor);
        }
        catch (Exception e)
        {
            final String msg = "Exception while waiting task completion " + taskmor.get_value();
            throw new EsxiUtilsException(msg, e);
        }

        if (taskResult.equalsIgnoreCase("success"))
        {
            return; // any exception
        }
        else
        {
            throw new EsxiUtilsException("Task " + taskmor.get_value() + " FAIL :" + taskResult);
        }
    }

    /**
     * Private helper to get an array dinamic property
     * 
     * @param MOR the Managed Object Referented
     * @param pName the property name
     * @return the array dinamy property
     * @throws Exception
     */
    public DynamicProperty[] getDynamicProarray(ManagedObjectReference MOR, String pName)
        throws EsxiUtilsException
    {
        ObjectContent[] objContent = getObjectProperties(null, MOR, new String[] {pName});
        ObjectContent contentObj = objContent[0];
        DynamicProperty[] objArr = contentObj.getPropSet();

        return objArr;
    }

    /**
     * Private helper to monitor the task launched
     * 
     * @param tmor the task managed object reference
     * @throws Exception
     */
    public void monitorTask(ManagedObjectReference tmor) throws EsxiUtilsException
    {
        if (tmor != null)
        {
            String result = waitForTask(tmor);
            if (result.equalsIgnoreCase("success"))
            {
                logger.info("Task Completed Sucessfully");
            }
            else
            {
                logger.error("Failure " + result);
                throw new EsxiUtilsException("The task could not be performed:\n" + result);
            }
        }
    }

    public String waitForTask(ManagedObjectReference taskmor) throws EsxiUtilsException
    {
        Object[] result =
            waitForValues(taskmor, new String[] {"info.state", "info.error"},
                new String[] {"state"}, new Object[][] {new Object[] {TaskInfoState.success,
                TaskInfoState.error}});

        if (result[0].equals(TaskInfoState.success))
        {
            return "success";
        }

        TaskInfo tinfo = (TaskInfo) getDynamicProperty(taskmor, "info");
        LocalizedMethodFault fault = tinfo.getError();

        // retry in 1second
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            throw new EsxiUtilsException("Interrupted while waiting for task ", e);
        }

        result =
            waitForValues(taskmor, new String[] {"info.state", "info.error"},
                new String[] {"state"}, new Object[][] {new Object[] {TaskInfoState.success,
                TaskInfoState.error}});

        if (result[0].equals(TaskInfoState.success))
        {
            return "success";
        }

        tinfo = (TaskInfo) getDynamicProperty(taskmor, "info");
        fault = tinfo.getError();

        if (fault == null || fault.getFault() == null)
        {
            return "Unknown Error Occurred";
        }

        return "Error Occurred :" + fault.getLocalizedMessage();
    }

    /**
     * Handle Updates for a single object. waits till expected values of properties to check are
     * reached Destroys the ObjectFilter when done.
     * 
     * @param objmor MOR of the Object to wait for</param>
     * @param filterProps Properties list to filter
     * @param endWaitProps Properties list to check for expected values these be properties of a
     *            property in the filter properties list
     * @param expectedVals values for properties to end the wait
     * @return true indicating expected values were met, and false otherwise
     */
    public Object[] waitForValues(ManagedObjectReference objmor, String[] filterProps,
        String[] endWaitProps, Object[][] expectedVals) throws EsxiUtilsException
    {
        // version string is initially null
        String version = "";
        Object[] endVals = new Object[endWaitProps.length];
        Object[] filterVals = new Object[filterProps.length];

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.setObjectSet(new ObjectSpec[] {creatObjectSpec(objmor, false, new SelectionSpec[] {})});

        spec.setPropSet(new PropertySpec[] {createPropertySpec(objmor.getType(), false, filterProps)});

        ManagedObjectReference filterSpecRef;
        try
        {
            filterSpecRef =
                getServiceInstance().getServerConnection().getVimService()
                    .createFilter(getPropertyCollector(), spec, true);
        }
        catch (InvalidProperty e1)
        {
            throw new EsxiUtilsException(e1);
        }
        catch (RuntimeFault e1)
        {
            throw new EsxiUtilsException(e1);
        }
        catch (RemoteException e1)
        {
            throw new EsxiUtilsException(e1);
        }

        boolean reached = false;

        UpdateSet updateset = null;
        PropertyFilterUpdate[] filtupary = null;
        PropertyFilterUpdate filtup = null;
        ObjectUpdate[] objupary = null;
        ObjectUpdate objup = null;
        PropertyChange[] propchgary = null;
        PropertyChange propchg = null;
        while (!reached)
        {
            boolean retry = true;
            while (retry)
            {
                try
                {
                    updateset =
                        getServiceInstance().getServerConnection().getVimService()
                            .waitForUpdates(getPropertyCollector(), version);
                    retry = false;
                }
                catch (Exception e)
                {
                    if (e instanceof org.apache.axis.AxisFault)
                    {
                        org.apache.axis.AxisFault fault = (org.apache.axis.AxisFault) e;
                        org.w3c.dom.Element[] errors = fault.getFaultDetails();
                        String faultString = fault.getFaultString();
                        if (faultString.indexOf("java.net.SocketTimeoutException") != -1)
                        {
                            logger.debug("Retrying2 wait for task duet SocketTimeout ........");
                            retry = true;
                        }
                        else
                        {
                            throw new EsxiUtilsException(e);
                        }
                    }
                }
            }
            if (updateset == null || updateset.getFilterSet() == null)
            {
                continue;
            }
            else
            {
                version = updateset.getVersion();
            }

            // Make this code more general purpose when PropCol changes later.
            filtupary = updateset.getFilterSet();
            filtup = null;
            for (int fi = 0; fi < filtupary.length; fi++)
            {
                filtup = filtupary[fi];
                objupary = filtup.getObjectSet();
                objup = null;
                propchgary = null;
                for (int oi = 0; oi < objupary.length; oi++)
                {
                    objup = objupary[oi];

                    // TODO: Handle all "kind"s of updates.
                    if (objup.getKind() == ObjectUpdateKind.modify
                        || objup.getKind() == ObjectUpdateKind.enter
                        || objup.getKind() == ObjectUpdateKind.leave)
                    {
                        propchgary = objup.getChangeSet();
                        for (int ci = 0; ci < propchgary.length; ci++)
                        {
                            propchg = propchgary[ci];
                            updateValues(endWaitProps, endVals, propchg);
                            updateValues(filterProps, filterVals, propchg);
                        }
                    }
                }
            }

            Object expctdval = null;
            // Check if the expected values have been reached and exit the loop if done.
            // Also exit the WaitForUpdates loop if this is the case.
            for (int chgi = 0; chgi < endVals.length && !reached; chgi++)
            {
                for (int vali = 0; vali < expectedVals[chgi].length && !reached; vali++)
                {
                    expctdval = expectedVals[chgi][vali];

                    reached = expctdval.equals(endVals[chgi]) || reached;
                }
            }
        }

        // Destroy the filter when we are done.
        try
        {
            getServiceInstance().getServerConnection().getVimService()
                .destroyPropertyFilter(filterSpecRef);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        return filterVals;
    }

    protected void updateValues(String[] props, Object[] vals, PropertyChange propchg)
    {
        for (int findi = 0; findi < props.length; findi++)
        {
            if (propchg.getName().lastIndexOf(props[findi]) >= 0)
            {
                if (propchg.getOp() == PropertyChangeOp.remove)
                {
                    vals[findi] = "";
                }
                else
                {
                    vals[findi] = propchg.getVal();
                    // System.out.println("Changed value : " + propchg.getVal());
                }
            }
        }
    }

    /**
     * This code takes an array of [typename, property, property, ...] and converts it into a
     * PropertySpec[]. handles case where multiple references to the same typename are specified.
     * 
     * @param typeinfo 2D array of type and properties to retrieve
     * @return Array of container filter specs
     */
    public PropertySpec[] buildPropertySpecArray(String[][] typeinfo)
    {
        // Eliminate duplicates
        HashMap tInfo = new HashMap();
        for (int ti = 0; ti < typeinfo.length; ++ti)
        {
            Set props = (Set) tInfo.get(typeinfo[ti][0]);
            if (props == null)
            {
                props = new HashSet();
                tInfo.put(typeinfo[ti][0], props);
            }
            boolean typeSkipped = false;
            for (int pi = 0; pi < typeinfo[ti].length; ++pi)
            {
                String prop = typeinfo[ti][pi];
                if (typeSkipped)
                {
                    props.add(prop);
                }
                else
                {
                    typeSkipped = true;
                }
            }
        }

        // Create PropertySpecs
        ArrayList pSpecs = new ArrayList();
        for (Iterator ki = tInfo.keySet().iterator(); ki.hasNext();)
        {
            String type = (String) ki.next();
            PropertySpec pSpec = new PropertySpec();
            Set<String> props = (Set<String>) tInfo.get(type);
            pSpec.setType(type);
            pSpec.setAll(props.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
            pSpec.setPathSet(props.toArray(new String[props.size()]));

            pSpecs.add(pSpec);
        }

        return (PropertySpec[]) pSpecs.toArray(new PropertySpec[0]);
    }

    /**
     * Determines of a method 'methodName' exists for the Object 'obj'
     * 
     * @param obj The Object to check
     * @param methodName The method name
     * @param parameterTypes Array of Class objects for the parameter types
     * @return true if the method exists, false otherwise
     */
    boolean methodExists(Object obj, String methodName, Class[] parameterTypes)
    {
        boolean exists = false;
        try
        {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            if (method != null)
            {
                exists = true;
            }
        }
        catch (Exception e)
        {
        }
        return exists;
    }

    private ManagedObjectReference getPropertyCollector()
    {
        return getServiceInstance().getPropertyCollector().getMOR();
    }

    //
    // public VimPortType getVimService()
    // {
    // return getServiceInstance().getServerConnection().getVimService();
    // }

    // public void browseMOR(ManagedObjectReference MOR)
    // {
    // try
    // {
    // ObjectContent[] ocary = getContentsRecursively(MOR, true);
    // ObjectContent oc = null;
    // ManagedObjectReference mor = null;
    // DynamicProperty[] pcary = null;
    // DynamicProperty pc = null;
    // if (ocary != null)
    // {
    // for (int oci = 0; oci < ocary.length; oci++)
    // {
    // oc = ocary[oci];
    // mor = oc.getObj();
    // pcary = oc.getPropSet();
    // if (pcary != null)
    // {
    // for (int pci = 0; pci < pcary.length; pci++)
    // {
    // pc = pcary[pci];
    // if (pc.getName().equalsIgnoreCase("name"))
    // System.out.println(pc.getVal().toString());
    //
    // }
    // }
    // }
    // }
    //
    // }
    // catch (Exception e)
    // {
    // System.out.println("ClassCastException");
    // e.printStackTrace();
    // }
    // }
    //
    //
    // public void browseArrayList(ArrayList arrList)
    // {
    // try
    // {
    //
    // Iterator iterator = arrList.iterator();
    // while (iterator.hasNext())
    // {
    // ObjectContent[] ocary =
    // getContentsRecursively(
    // (ManagedObjectReference) iterator.next(), true);
    // ObjectContent oc = null;
    // ManagedObjectReference mor = null;
    // DynamicProperty[] pcary = null;
    // DynamicProperty pc = null;
    // if (ocary != null)
    // {
    // for (int oci = 0; oci < ocary.length; oci++)
    // {
    // oc = ocary[oci];
    // mor = oc.getObj();
    // pcary = oc.getPropSet();
    // if (pcary != null)
    // {
    // for (int pci = 0; pci < pcary.length; pci++)
    // {
    // pc = pcary[pci];
    // if (pc.getName().equalsIgnoreCase("name"))
    // System.out.println(pc.getVal());
    // }
    // }
    // }
    // }
    // }
    // }
    // catch (Exception e)
    // {
    // System.out.println(" Exceptions ");
    // e.printStackTrace();
    // }
    // }

    // / VmwareMachineUtils

    public HostSystem getHostSystem() throws EsxiUtilsException
    {
        ManagedEntity[] mes;
        try
        {
            mes =
                new InventoryNavigator(getServiceInstance().getRootFolder())
                    .searchManagedEntities("HostSystem");
        }
        catch (MethodFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        if (mes == null || mes.length < 1)
        {
            throw new EsxiUtilsException("Host System not found");
        }
        else if (mes.length > 1)
        {
            logger.warn("There are more than a single Host System, using the first.");
        }

        HostSystem host = (HostSystem) mes[0];

        return host;
    }

    /**
     * Gets the host system on the current datacenter.
     */
    public ManagedObjectReference getHostSystemMOR() throws EsxiUtilsException
    {
        ManagedObjectReference dcmor;
        ManagedObjectReference hfmor;

        String dcName = getSessionOption("datacentername");

        try
        {
            dcmor = getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            String msg = "Datacenter [" + dcName + "] not found.";
            logger.error(msg);
            throw new EsxiUtilsException(msg);
        }

        try
        {
            hfmor = getMoRefProp(dcmor, "hostFolder");
        }
        catch (Exception e)
        {
            String msg = "Datacenter " + dcName + " not found.";
            logger.error(msg);
            throw new EsxiUtilsException(msg);
        }

        // crmors = getServiceUtil().getDecendentMoRefs(hfmor, "ComputeResource");

        return getHostSystemMor(dcmor, hfmor);
    }

    /**
     * Gets the host system from a given datacenter and host folder. If not specified the
     * ''hostname'' option use the first decendent on the datacenter.
     */
    public ManagedObjectReference getHostSystemMor(ManagedObjectReference dcmor,
        ManagedObjectReference hfmor) throws EsxiUtilsException
    {
        ManagedObjectReference hostmor;
        String hostName = getSessionOption("hostname");

        if (hostName != null)
        {
            try
            {
                hostmor = getDecendentMoRef(hfmor, "HostSystem", hostName);

                if (hostmor == null)
                {
                    throw new EsxiUtilsException("HostSystem not found");
                }
            }
            catch (Exception e)
            {
                String message = "Host " + hostName + " not found";
                logger.error(message);
                throw new EsxiUtilsException(message);
            }
        }
        else
        {
            try
            {
                hostmor = getFirstDecendentMoRef(dcmor, "HostSystem");
            }
            catch (Exception e)
            {
                String message = "Host " + hostName + " not found using ''FirstDecendent''";
                logger.error(message);
                throw new EsxiUtilsException(message);
            }
        }

        return hostmor;
    }

    /**
     * Get the computer resource associated to the given host.
     * 
     * @param crmors, all the computer resource on the data center.
     * @param hostmor, reference to the host related to the current VM.
     */
    public ManagedObjectReference getComputerResourceFromHost(
        ArrayList<ManagedObjectReference> crmors, ManagedObjectReference hostmor)
        throws EsxiUtilsException
    {

        ManagedObjectReference crmor = null;
        String hostName;

        try
        {
            hostName = (String) getDynamicProperty(hostmor, "name");
        }
        catch (Exception e)
        {
            throw new EsxiUtilsException("Can not get ''name'' property for the given HostMOR");
        }

        for (int i = 0; i < crmors.size(); i++)
        {
            try
            {
                ManagedObjectReference[] hrmors =
                    (ManagedObjectReference[]) getDynamicProperty(
                        (ManagedObjectReference) crmors.get(i), "host");

                if (hrmors != null && hrmors.length > 0)
                {
                    for (int j = 0; j < hrmors.length; j++)
                    {
                        String hname = (String) getDynamicProperty(hrmors[j], "name");
                        if (hname.equalsIgnoreCase(hostName))
                        {
                            crmor = (ManagedObjectReference) crmors.get(i);
                            i = crmors.size() + 1;
                            j = hrmors.length + 1;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                final String msg = "Can not get host on the computer resource ";
                throw new EsxiUtilsException(msg, e);
            }
        }

        if (crmor == null)
        {
            String message = "No Compute Resource Found On Specified Host";
            logger.error(message);
            throw new EsxiUtilsException(message);
        }

        return crmor;
    }

    /**
     * Gets the reference to the network with provided name on the data center.
     * 
     * @param networkName, the name of the desired network.
     * @return the reference to the network named networkName, if exists.
     * @throws VirtualMachineException if some error or the given name is not any network on the
     *             datacenter.
     */
    public ManagedObjectReference getNetwork(String networkName) throws EsxiUtilsException
    {

        String dcName; // datacenter name
        ManagedObjectReference dcmor; // datacenter
        ManagedObjectReference hfmor; // host folder
        ManagedObjectReference hostmor;// host
        ArrayList<ManagedObjectReference> crmors;// all computer resources on host folder
        ManagedObjectReference crmor; // computer resource
        ManagedObjectReference netMor = null;

        try
        {
            dcName = getSessionOption("datacentername");
            dcmor = getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                String message = "Datacenter " + dcName + " not found.";
                logger.error(message);
                throw new EsxiUtilsException(message);
            }

            hfmor = getMoRefProp(dcmor, "hostFolder");

            hostmor = getHostSystemMor(dcmor, hfmor);

            crmors = getDecendentMoRefs(hfmor, "ComputeResource");

            crmor = getComputerResourceFromHost(crmors, hostmor);

            // EsxiUtils vmutils = new EsxiUtils(apputil);
            ConfigTarget configTarget = getConfigTargetForHost(crmor, hostmor);

            boolean flag = false;
            for (int i = 0; i < configTarget.getNetwork().length; i++)
            {
                VirtualMachineNetworkInfo networkInfo = configTarget.getNetwork()[i];
                NetworkSummary networkSummary = networkInfo.getNetwork();

                if (networkSummary.getName().equals(networkName))
                {
                    flag = true;
                    if (networkSummary.isAccessible())
                    {
                        networkName = networkSummary.getName();
                        netMor = networkSummary.getNetwork();
                    }
                    else
                    {
                        throw new EsxiUtilsException("Specified Network is not accessible");
                    }
                    break;
                }
            }

            if (!flag || netMor == null)
            {
                logger.warn("Network [{}] not found ", networkName);
                return null;
            }

        }
        catch (Exception e)
        {
            throw new EsxiUtilsException("Can not get the Network " + networkName, e);
        }

        return netMor;
    }

    /**
     * Checks if exists the virtual switch in the hypervisors
     * 
     * @param hostmor the Host managed object reference
     * @param vSwitchName the virtual switch to check
     * @return true if the virtual switch exists, false if contrary
     * @throws Exception
     */
    public boolean existsVswitch(ManagedObjectReference hostmor, String vSwitchName)
    {

        Object hiobj = getDynamicProperty(hostmor, "config");
        HostConfigInfo hostConfigInfo = (HostConfigInfo) hiobj;
        HostNetworkInfo network = hostConfigInfo.getNetwork();
        for (HostVirtualSwitch vswitch : network.getVswitch())
        {
            if (vswitch.getName().equals(vSwitchName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the the Virtual machines list using the network (port group) passed as parameter
     * 
     * @param networkName the network name
     * @return the virtual machine list using this network
     * @throws VirtualMachineException
     */
    public ManagedObjectReference[] getVmsFromNetworkName(String networkName)
        throws EsxiUtilsException
    {

        try
        {
            ManagedObjectReference network = getNetwork(networkName);
            ManagedObjectReference[] vms =
                (ManagedObjectReference[]) getDynamicProperty(network, "vm");

            if (vms == null)
            {
                // TODO why not null brother ??
                vms = new ManagedObjectReference[0];
            }
            return vms;
        }
        catch (Exception e)
        {
            throw new EsxiUtilsException("An error was found when getting the virtual machine used by this network: "
                + networkName,
                e);
        }
    }

    /**
     * @deprecated should return only one instance. Private helper to choose the virtual machine
     *             managed object reference
     * @param vmname, the VM name.
     * @return a list of virtual machine managed object references retalted to the
     * @throws Exception
     */
    public ArrayList<ManagedObjectReference> getVms(String vmname) throws EsxiUtilsException
    {
        // String vmname = null;
        // String operation = null;
        String host = null;
        String folder = null;
        String datacenter = null;
        String pool = null;
        String guestid = null;
        String ipaddress = null;
        String[][] filter = null;

        // ExtendedAppUtil cb = vmwareHyper.getAppUtil();
        ArrayList<ManagedObjectReference> vmList = new ArrayList<ManagedObjectReference>();

        if (isSessionOptionSet("host"))
        {
            host = getSessionOption("host");
        }
        if (isSessionOptionSet("folder"))
        {
            folder = getSessionOption("folder");
        }
        if (isSessionOptionSet("datacenter"))
        {
            datacenter = getSessionOption("datacenter");
        }
        if (isSessionOptionSet("vmname"))
        {
            vmname = getSessionOption("vmname"); // XXX
        }
        if (isSessionOptionSet("pool"))
        {
            pool = getSessionOption("pool");
        }
        if (isSessionOptionSet("ipaddress"))
        {
            ipaddress = getSessionOption("ipaddress");
        }
        if (isSessionOptionSet("guestid"))
        {
            guestid = getSessionOption("guestid");
        }
        // filter = new String[][] { new String[] { "summary.config.guestId", "winXPProGuest",},};
        // vmname = this.machineName;

        filter =
            new String[][] {new String[] {"guest.ipAddress", ipaddress,},
            new String[] {"summary.config.guestId", guestid,}};

        vmList = getVMs("VirtualMachine", datacenter, folder, pool, vmname, host, filter);

        return vmList;
    }

    // TODO vm o vms ??
    @SuppressWarnings("unchecked")
    private ArrayList<ManagedObjectReference> getVM(ManagedObjectReference tempMOR,
        String[][] filterData) throws EsxiUtilsException
    {
        return (ArrayList<ManagedObjectReference>) getDecendentMoRefs(tempMOR, "VirtualMachine",
            filterData);
    }

    /**
     * Gets the management object reference from the virtual machine name
     * 
     * @param vmName the virtual machine name
     * @throws Exception
     */
    public ManagedObjectReference getVmMor(String vmName) throws EsxiUtilsException
    {
        return getVm(vmName).getMOR();
    }

    public VirtualMachine getVm(String vmName) throws EsxiUtilsException
    {
        Folder rootFolder = getServiceInstance().getRootFolder();
        VirtualMachine vm;
        try
        {
            vm =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", vmName);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(String.format("Can not obtain Virtual Machine %s ", vmName),
                e);
        }

        if (vm == null)
        {
            throw new EsxiUtilsException(String.format("Virtual Machine %s not found", vmName));
        }

        return vm;
    }

    public VirtualMachinePowerState getVmState(String vmName) throws EsxiUtilsException
    {
        getVm(vmName).getRuntime().getPowerState();
    }

    public boolean isVMAlreadyCreated(String vmUuid)
    {
        Folder rootFolder = getServiceInstance().getRootFolder();
        VirtualMachine vm;
        try
        {
            vm =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", vmUuid);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(String.format("Can not obtain Virtual Machine %s ", vmUuid),
                e);
        }

        return vm != null;

    }

    private ArrayList<ManagedObjectReference> getNetwork(ManagedObjectReference tempMOR,
        String[][] filterData) throws EsxiUtilsException
    {
        return (ArrayList<ManagedObjectReference>) getDecendentMoRefs(tempMOR, "Network",
            filterData);
    }

    /**
     * Private helper to get the virtual machine power state
     * 
     * @param vmmor the MOR to get the info from
     * @return the virtual machine state
     * @throws Exception if there is any error
     */
    public VirtualMachinePowerState getVMState(ManagedObjectReference vmmor)
        throws EsxiUtilsException
    {
        DynamicProperty[] virtualMachineRuntimeInfoProperty;
        VirtualMachineRuntimeInfo runtimeInfo;
        virtualMachineRuntimeInfoProperty = getDynamicProarray(vmmor, "runtime");
        runtimeInfo = ((VirtualMachineRuntimeInfo) (virtualMachineRuntimeInfoProperty[0]).getVal());
        return runtimeInfo.getPowerState();
    }

    // //

    // Retrieve properties from a single MoRef
    public Object[] getProperties(ManagedObjectReference moRef, String[] properties)
        throws EsxiUtilsException
    {
        ServiceContent content = getServiceInstance().getServiceContent();
        PropertySpec pSpec = new PropertySpec();
        pSpec.setType(moRef.getType());
        pSpec.setPathSet(properties);

        ObjectSpec oSpec = new ObjectSpec();
        // Set the starting object
        oSpec.setObj(moRef);
        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        pfSpec.setPropSet(new PropertySpec[] {pSpec});
        pfSpec.setObjectSet(new ObjectSpec[] {oSpec});
        ObjectContent[] ocs;

        try
        {
            ocs =
                getServiceInstance()
                    .getServerConnection()
                    .getVimService()
                    .retrieveProperties(content.getPropertyCollector(),
                        new PropertyFilterSpec[] {pfSpec});
        }
        catch (InvalidProperty e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RuntimeFault e)
        {
            throw new EsxiUtilsException(e);
        }
        catch (RemoteException e)
        {
            throw new EsxiUtilsException(e);
        }

        Object[] ret = new Object[properties.length];

        if (ocs != null)
        {
            for (int i = 0; i < ocs.length; ++i)
            {
                ObjectContent oc = ocs[i];
                DynamicProperty[] dps = oc.getPropSet();
                if (dps != null)
                {
                    for (int j = 0; j < dps.length; ++j)
                    {
                        DynamicProperty dp = dps[j];
                        for (int p = 0; p < ret.length; ++p)
                        {
                            if (properties[p].equals(dp.getName()))
                            {
                                logger.debug("property named [{}] value [{}]", dp.getName(), dp
                                    .getVal().toString());

                                ret[p] = dp.getVal();
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Gets the resource allocation information from the resource value
     * 
     * @param value the value
     * @return the resource allocation information
     * @throws Exception
     */
    public ResourceAllocationInfo createShares(String value)
    {
        ResourceAllocationInfo raInfo = new ResourceAllocationInfo();
        SharesInfo sharesInfo = new SharesInfo();

        if (value.equalsIgnoreCase(SharesLevel.high.name()))
        {
            sharesInfo.setLevel(SharesLevel.high);
        }
        else if (value.equalsIgnoreCase(SharesLevel.normal.name()))
        {
            sharesInfo.setLevel(SharesLevel.normal);
        }
        else if (value.equalsIgnoreCase(SharesLevel.low.name()))
        {
            sharesInfo.setLevel(SharesLevel.low);
        }
        else
        {
            sharesInfo.setLevel(SharesLevel.custom);
            sharesInfo.setShares(Integer.parseInt(value));
        }
        raInfo.setShares(sharesInfo);
        return raInfo;
    }

}
