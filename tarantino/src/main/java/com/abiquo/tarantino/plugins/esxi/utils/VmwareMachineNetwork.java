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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualNIC;
import com.abiquo.tarantino.errors.VirtualFactoryError;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.plugins.esxi.utils.EsxiUtils.EsxiUtilsException;
import com.vmware.vim25.ConfigTarget;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineNetworkInfo;

/**
 * @author apuig (based on the great work of Pedro Navarro)
 * */
public class VmwareMachineNetwork
{

//    private final static Logger logger = LoggerFactory.getLogger(VmwareMachineNetwork.class);

    private EsxiUtils utils;

    public VmwareMachineNetwork(EsxiUtils utils)
    {
        this.utils = utils;
    }

    private String getNetworkName(ConfigTarget configTarget) throws VirtualFactoryException
    {
        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        String networkName = null;

        if (configTarget.getNetwork() != null)
        {
            for (int i = 0; i < configTarget.getNetwork().length; i++)
            {
                VirtualMachineNetworkInfo netInfo = configTarget.getNetwork()[i];
                NetworkSummary netSummary = netInfo.getNetwork();
                if (netSummary.isAccessible())
                {
                    networkName = netSummary.getName();
                    break;
                }
            }
        }

        if (networkName == null)
        {
            throw new VirtualFactoryException(VirtualFactoryError.NETWORK_NOT_FOUND);
        }

        return networkName;
    }

    // TODO this seems deprected by the next function
    /**
     * Configures the network interfaces from a virtual nic list
     * 
     * @param vnicList the virtual nic list to configure
     * @return
     * @throws EsxiUtilsException
     */
    public List<VirtualDeviceConfigSpec> configureNetworkInterfaces(List<VirtualNIC> vnicList)
        throws VirtualFactoryException
    {
        List<VirtualDeviceConfigSpec> nicSpecList = new ArrayList<VirtualDeviceConfigSpec>();

        try
        {

            int index = 1;
            for (VirtualNIC vnic : vnicList)
            {
                // Try to find if a group corresponding the network name is found. If not create it.
                String networkName = vnic.getNetworkName() + "_" + vnic.getVlanTag();
                ManagedObjectReference networkMor = utils.getNetwork(networkName);

                VirtualEthernetCardNetworkBackingInfo nicBacking =
                    new VirtualEthernetCardNetworkBackingInfo();
                nicBacking.setDeviceName(networkName);
                nicBacking.setNetwork(networkMor);

                VirtualEthernetCard nic2 = new VirtualE1000();
                nic2.setAddressType("manual");
                nic2.setMacAddress(vnic.getMacAddress());
                nic2.setBacking(nicBacking);
                nic2.setKey(4);

                VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
                nicSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
                nicSpec.setDevice(nic2);

                nicSpecList.add(nicSpec);
                index++;
            }
            return nicSpecList;
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryError.NETWORK_CONFIGURATION);
        }
    }

    /**
     * Configures the port groups and tags the corresponding VLAN
     * 
     * @throws Exception
     */
    public void configureNetwork(List<VirtualNIC> vnicList) throws VirtualFactoryException
    {

        // Creating or updating the needed port group and tagging
        for (VirtualNIC vnic : vnicList)
        {
            String portGroupName = vnic.getNetworkName() + "_" + vnic.getVlanTag();
            // Try to find if a group corresponding the network name is found. If not create it.
            ManagedObjectReference networkMor = utils.getNetwork(portGroupName);
            ManagedObjectReference hostmor = utils.getHostSystemMOR();

            if (networkMor == null)
            {
                Object cmobj = utils.getDynamicProperty(hostmor, "configManager");

                HostConfigManager configMgr = (HostConfigManager) cmobj;
                ManagedObjectReference nwSystem = configMgr.getNetworkSystem();

                HostPortGroupSpec portgrp = new HostPortGroupSpec();
                portgrp.setName(portGroupName);

                if (utils.existsVswitch(hostmor, vnic.getVSwitchName()))
                {
                    portgrp.setVswitchName(vnic.getVSwitchName());
                }
                else
                {
                    throw new VirtualFactoryException(VirtualFactoryError.NETWORK_VSWITCH_NOT_FOUND,
                        vnic.getVSwitchName());
                }

                portgrp.setPolicy(new HostNetworkPolicy());
                portgrp.setVlanId(Integer.valueOf(vnic.getVlanTag()));

                utils.logger.debug("Adding port group: " + portGroupName + " tagged with VLAN: "
                    + vnic.getVlanTag() + " to Virtual Switch " + vnic.getVSwitchName());

                try
                {
                    utils.getServiceInstance().getServerConnection().getVimService()
                        .addPortGroup(nwSystem, portgrp);
                }
                catch (Exception e)
                {

                    final String detail =
                        String.format(
                            "Adding port group: %s  tagged with VLAN: %s to Virtual Switch %s",
                            portGroupName, vnic.getVlanTag(), vnic.getVSwitchName());

                    throw new VirtualFactoryException(VirtualFactoryError.NETWORK_CONFIGURATION,
                        detail);
                }
            }
            else
            {

                HostConfigInfo hostConfigInfo =
                    (HostConfigInfo) utils.getDynamicProperty(hostmor, "config");

                HostNetworkInfo network = hostConfigInfo.getNetwork();

                for (HostVirtualSwitch vswitch : network.getVswitch())
                {
                    if (!vswitch.getName().equals(vnic.getVSwitchName()))
                    {
                        String[] portGroups = vswitch.getPortgroup();
                        for (String portGroup : portGroups)
                        {
                            // If the port group of the vSwitch ends with the name of the port group
                            // name
                            // It means somebody has attached the same port group to another switch.
                            // FAIL!
                            if (portGroup.endsWith("-" + portGroupName) == true)
                            {
                                throw new VirtualFactoryException(VirtualFactoryError.NETWORK_VSWITCH_PORT,
                                    String.format(
                                        "portGroupName:%s \n vswitch:%s \n vnicswitch:%s",
                                        portGroupName, vswitch.getName(), vnic.getVSwitchName()));

                            }
                        }
                    }
                }
            }
        }// nic bucle

    }

    /**
     * Deconfigures the switch groups used by the Virtual machine
     * 
     * @throws VirtualMachineException
     */
    public void deconfigureNetwork(List<VirtualNIC> vnicList) throws VirtualFactoryException
    {
        // Just deletes the switch groups if they are just used by the VM to delete

        try
        {
            for (VirtualNIC vnic : vnicList)
            {
                String portGroup = vnic.getNetworkName() + "_" + vnic.getVlanTag();

                ManagedObjectReference[] vmsUsedByNetwork = utils.getVmsFromNetworkName(portGroup);

                if (vmsUsedByNetwork.length == 0)
                {
                    utils.logger.debug("There is no virtual machine using network: " + portGroup
                        + " proceeding to delete");

                    ManagedObjectReference hostmor = utils.getHostSystemMOR();
                    Object cmobj = utils.getDynamicProperty(hostmor, "configManager");
                    HostConfigManager configMgr = (HostConfigManager) cmobj;
                    ManagedObjectReference nwSystem = configMgr.getNetworkSystem();

                    utils.getServiceInstance().getServerConnection().getVimService()
                        .removePortGroup(nwSystem, portGroup);
                    utils.logger.debug("Removing port group: " + portGroup);
                }

            }
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryError.NETWORK_DECONFIGURE,
                e.toString());
        }
    }
}
