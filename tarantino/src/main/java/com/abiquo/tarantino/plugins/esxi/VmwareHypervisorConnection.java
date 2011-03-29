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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.HypervisorConnection;
import com.abiquo.tarantino.errors.VirtualFactoryErrors;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.plugins.esxi.utils.EsxiUtils;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

// TODO @Hypervisor(type = HypervisorType.VMX_04)
public class VmwareHypervisorConnection implements IHypervisorConnection
{
    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(VmwareHypervisorConnection.class);

    /**
     * ESXi session
     */
    private EsxiUtils esxi;

    public EsxiUtils getUtils()
    {
        return esxi;
    }

    @Override
    public void login(HypervisorConnection connection) throws VirtualFactoryException
    {

        try
        {
            logout();

            esxi = createConnection(connection);

            esxi.getUtilBasics().checkLicense();
            String datasoreRepositoryName =
                esxi.getUtilBasics().getAndCheckRepositoryDatastore(
                    globalConfig.getRepositoryLocation());
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

    @Override
    public void logout()
    {
        if (esxi != null && esxi.isConnected())
        {
            esxi.disConnect();
        }
    }

    private EsxiUtils createConnection(HypervisorConnection connection)
        throws VirtualFactoryException
    {

        ServiceInstance serviceInstance;
        try
        {
            final String urlstr = connection.getConnectionURI();

            final URL hUrl = new URL(urlstr);

            serviceInstance =
                new ServiceInstance(hUrl,
                    connection.getLoginUser(),
                    connection.getLoginPassword(),
                    globalConfig.ignoreCert());

            return new EsxiUtils(serviceInstance, constructOptions(), builtinOptionsEntered(
                connection, hUrl.toString()));
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

}
