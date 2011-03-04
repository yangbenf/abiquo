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

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.HypervisorConnection;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.VirtualMachine;
import com.abiquo.tarantino.errors.VirtualFactoryErrors;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisor;
import com.abiquo.tarantino.plugins.esxi.utils.EsxiUtils;
import com.abiquo.tarantino.plugins.esxi.utils.VmwareMachineUtils;
import com.abiquo.tarantino.virtualmachine.IVirtualMachine;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

// TODO @Hypervisor(type = HypervisorType.VMX_04)
public class VmwareHypervisor implements IHypervisor
{

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(VmwareHypervisor.class.getName());

    /**
     * ESXi session
     */
    private EsxiUtils esxi;

    private VmwareMachineUtils utils;

    public VmwareMachineUtils getUtilsVm()
    {
        return utils;
    }

    public EsxiUtils getUtilsEsxi()
    {
        return esxi;
    }

    @Override
    public void connectAndLogin(HypervisorConnection hconn) throws VirtualFactoryException
    {

        try
        {
            logout();

            esxi = createConnection(hconn);

            utils = new VmwareMachineUtils(esxi);

            utils.checkLicense();
            String datasoreRepositoryName =
                utils.getAndCheckRepositoryDatastore(globalConfig.getRepositoryLocation());
            // TODO WTF set set set
            globalConfig.setRepositoryDatastore(datasoreRepositoryName);

        }
        catch (VirtualFactoryException vfe)
        {
            logger.debug("An error was occurred when connecting to the hypervisor", vfe);

            logout();

            throw vfe;
        }
    }

    private EsxiUtils createConnection(HypervisorConnection hconn) throws VirtualFactoryException
    {

        ServiceInstance serviceInstance;
        try
        {
            final URL hUrl =
                new URL(String.format("%s://%s:%s/sdk", hconn.getProtocol(), hconn.getIp(),
                    hconn.getPort()));

            serviceInstance =
                new ServiceInstance(hUrl,
                    hconn.getLoginUser(),
                    hconn.getLoginPassword(),
                    globalConfig.ignoreCert());

            return new EsxiUtils(serviceInstance, constructOptions(), builtinOptionsEntered(hconn,
                hUrl.toString()));
        }
        catch (RemoteException e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION,
                e.getMessage());
        }
        catch (MalformedURLException e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION,
                e.getMessage());
        }
    }

    @Override
    public void logout()
    {
        if (esxi != null && esxi.isConnected())
        {
            esxi.disConnect();
        }
    }

    @Override
    public IVirtualMachine createMachine(VirtualMachine vmachine)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IVirtualMachine getMachine(String vmachineId)
    {
        // // Gets the VMWare API main interface
        // init(this.url, this.user, this.password);
        // connect(this.url);

        // TODO connectAndLogin

        try
        {
            // ManagedObjectReference machinemor =
            // utils.getVmMor(virtualMachineConfig.getMachineName());

            ManagedObjectReference machinemoruuid = esxi.getVmMor(vmachineId);

            if (machinemoruuid != null)
            {
                virtualMachineConfig.setHypervisor(this);
                AbsVirtualMachine vm = createMachine(virtualMachineConfig);
                vm.setState(State.DEPLOYED);
                return vm;
            }
            else
            {
                throw new VirtualFactoryException(VirtualFactoryErrors.VIRTUAL_MACHINE_NOT_FOUND,
                    vmachineId);
            }
        }
        catch (Exception e) // EsxiUtilsException
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.VIRTUAL_MACHINE_RETRIEVE_ERROR,
                e.getMessage());
        }

        // TODO at the end logout
        // finally
        // {
        // logout();
        // }
    }

    private VmwareHypervisorConfig globalConfig = new VmwareHypervisorConfig();

    private Map<String, String> builtinOptionsEntered(HypervisorConnection hconn,
        String urlConnection)
    {
        HashMap<String, String> optsEntered = new HashMap<String, String>();

        optsEntered.put("url", urlConnection);

        optsEntered.put("username", hconn.getLoginUser());
        optsEntered.put("password", hconn.getLoginPassword());

        // global configuration
        optsEntered.put("ignorecert", String.valueOf(globalConfig.ignoreCert()));
        optsEntered.put("datacentername", globalConfig.getDatacenterName());

        return optsEntered;
    }

    /**
     * It constructs the basic options needed to work
     * 
     * @return
     */
    private static OptionSpec[] constructOptions()
    {
        OptionSpec[] useroptions = new OptionSpec[7];
        useroptions[0] = new OptionSpec("vmname", "String", 1, "Name of the virtual machine", null);
        useroptions[1] =
            new OptionSpec("datacentername", "String", 1, "Name of the datacenter", null);

        // TODO usefull ??
        useroptions[2] = new OptionSpec("hostname", "String", 0, "Name of the host", null);
        useroptions[3] =
            new OptionSpec("guestosid", "String", 0, "Type of Guest OS", "winXPProGuest");
        useroptions[4] = new OptionSpec("cpucount", "Integer", 0, "Total CPU Count", "1");
        useroptions[5] = new OptionSpec("disksize", "Integer", 0, "Size of the Disk", "64");
        useroptions[6] =
            new OptionSpec("memorysize",
                "Integer",
                0,
                "Size of the Memory in the blocks of 1024 MB",
                "1024");
        return useroptions;
    }

    class VmwareHypervisorConfig
    {
        private boolean ignorecert;

        private String datacentername;

        // datastore name used as abiquo repository (configured nfs)
        private String repositoryLocation;

        // obtained based on the ''repositoryLocation''
        private String repositoryDatastore;

        private final static String IGNORECERT_DEFAULT = "true";

        private final static String DATACENTERNAME_DEFAULT = "ha-datacenter";

        public VmwareHypervisorConfig()
        {
            ignorecert =
                Boolean.parseBoolean(System.getProperty(
                    "com.abiquo.virtualfactory.esxi.ignorecert", IGNORECERT_DEFAULT));
            datacentername =
                System.getProperty("com.abiquo.virtualfactory.esxi.datacentername",
                    DATACENTERNAME_DEFAULT);

            repositoryLocation =
                System.getProperty("abiquo.virtualfactory.vmware.repositoryLocation");

            if (repositoryLocation == null || repositoryLocation.isEmpty()
                || !repositoryLocation.contains(":"))
            {
                throw new RuntimeException("Invalid or missing ''repositoryLocation'' attribute required for ESXi hypervisor");
            }
        }

        public String getDatacenterName()
        {
            return datacentername;
        }

        public boolean ignoreCert()
        {
            return ignorecert;
        }

        public String getRepositoryLocation()
        {
            return repositoryLocation;
        }

        public String getRepositoryDatastore()
        {
            return repositoryDatastore;
        }

        public void setRepositoryDatastore(String repositoryDatastore)
        {
            this.repositoryDatastore = repositoryDatastore;
        }

    }

    // // //
    // private void copyDataStorefile() throws Exception
    // {
    // Configuration mainConfig =
    // AbiCloudModel.getInstance().getConfigManager().getConfiguration();
    // VmwareHypervisorConfiguration config = mainConfig.getVmwareHyperConfig();
    // // String dcName = apputil.get_option("datacentername");
    // String dcName = config.getDatacenterName();
    // ManagedObjectReference dcmor = serviceUtil.getDecendentMoRef(null, "Datacenter", dcName);
    // ManagedObjectReference fileManager =
    // apputil.getServiceInstance().getServiceContent().getFileManager();
    // ManagedObjectReference taskCopyMor =
    // serviceUtil.getVimService().copyDatastoreFile_Task(fileManager,
    // "[nfsrepository] ubuntu810desktop/ubuntu810desktop-flat.vmdk", dcmor,
    // "[datastore1] test/test-flat.vmdk", dcmor, true);
    // /*
    // * ManagedObjectReference taskCopyMor =
    // * serviceUtil.getService().copyDatastoreFile_Task(fileManager,
    // * "[datastore1] testubuntu/testubuntu.vmdk", dcmor,
    // *
    // "[datastore1] 11b0b35e-4810-4aed-95c5-12b4dc06e80a/11b0b35e-4810-4aed-95c5-12b4dc06e80a.vmdk"
    // * , dcmor, true);
    // */
    // // ManagedObjectReference taskCopyMor =
    // // serviceUtil.getService().copyVirtualDisk_Task(virtualDiskManager,
    // // "[nfsrepository] ubuntu/Ubuntu.8.10.Server.vmdk", dcmor,
    // // "[datastore1] test/Nostalgia.vmdk", dcmor, null, true);
    // String res = serviceUtil.waitForTask(taskCopyMor);
    // if (res.equalsIgnoreCase("success"))
    // {
    // logger.info("Virtual Machine Created Sucessfully");
    // }
    // else
    // {
    // String message = "Virtual Machine could not be created. " + res;
    // logger.error(message);
    // throw new VirtualMachineException(message);
    // }
    // }
}
