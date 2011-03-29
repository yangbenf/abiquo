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

package com.abiquo.commons.amqp.impl.datacenter.domain.builder;

import com.abiquo.commons.amqp.impl.datacenter.domain.AuxiliaryDisk;
import com.abiquo.commons.amqp.impl.datacenter.domain.DiskDescription;
import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStateful;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualNIC;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.NetworkConfiguration;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.PrimaryDisk;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.SecondaryDisks;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition.PrimaryDisk.DiskStandardConfiguration;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.CreateVirtualMachine;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.HardwareConfiguration;

public class CreateVirtualMachineJobBuilder extends VirtualFactoryJobBuilder
{
    private HardwareConfiguration hardConf;

    private NetworkConfiguration netConf;

    private PrimaryDisk primaryDisk;

    private SecondaryDisks secondaryDisks;

    public CreateVirtualMachineJobBuilder connection(String hypervisorID, String hypervisortype,
        String ip, String port, String protocol, String loginUser, String loginPasswoed)
    {
        super
            .connection(hypervisorID, hypervisortype, ip, port, protocol, loginUser, loginPasswoed);
        return this;
    }

    public CreateVirtualMachineJobBuilder hardware(int virtualCpu, int ramInMb)
    {
        hardConf = new HardwareConfiguration();
        hardConf.setVirtualCpu(virtualCpu);
        hardConf.setRamInMb(ramInMb);

        return this;
    }

    public CreateVirtualMachineJobBuilder addNetwork(String vSwitchName, String macAddress,
        String networkName, String vlanTag, String sequence)
    {
        if (netConf == null)
        {
            netConf = new NetworkConfiguration();
        }

        VirtualNIC nic = new VirtualNIC();
        nic.setVSwitchName(vSwitchName);
        nic.setMacAddress(macAddress);
        nic.setNetworkName(networkName);
        nic.setVlanTag(vlanTag);
        nic.setSequence(sequence);
        
        netConf.getVirtualNIC().add(nic);

        return this;
    }

    public CreateVirtualMachineJobBuilder primaryDisk(String format, String capacity,
        String sourceDatastore, String sourcePath, String destinationDatastore)
    {

        DiskStandard disk = new DiskStandard();
        disk.setFormat(format);
        disk.setCapacity(capacity);
        disk.setDatastore(sourceDatastore);
        disk.setPath(sourcePath);

        VirtualMachineDefinition.PrimaryDisk.DiskStandardConfiguration standard = new DiskStandardConfiguration();
        standard.setDiskStandard(disk);
        standard.setDestinationDatastore(destinationDatastore);

        primaryDisk = new PrimaryDisk();
        primaryDisk.setDiskStandardConf(standard);
        return this;
    }

    public CreateVirtualMachineJobBuilder primaryDisk(String format, String capacity, String iqn)
    {
        DiskStateful disk = new DiskStateful();
        disk.setFormat(format);
        disk.setCapacity(capacity);
        disk.setIqn(iqn);

        primaryDisk = new PrimaryDisk();
        primaryDisk.setDiskStatefull(disk);
        return this;
    }

    public CreateVirtualMachineJobBuilder addAuxDisk(String format, String capacity, String iqn,
        int sequence)
    {
        if (secondaryDisks == null)
        {
            secondaryDisks = new SecondaryDisks();
        }

        AuxiliaryDisk auxDisk = new AuxiliaryDisk();
        // auxDisk.setDiskID(value); // XXX unset
        auxDisk.setFormat(format);
        auxDisk.setCapacity(capacity);
        auxDisk.setIqn(iqn);

        secondaryDisks.getAuxDisk().add(auxDisk);

        return this;
    }

    public CreateVirtualMachine build(String virtualMachineId)
    {
        VirtualMachineDefinition virtualMachine = new VirtualMachineDefinition();
        // TODO check not null
        virtualMachine.setMachineID(virtualMachineId);
        virtualMachine.setHardwareConf(hardConf);
        virtualMachine.setNetworkConf(netConf);
        virtualMachine.setPrimaryDisk(primaryDisk);
        virtualMachine.setSecondaryDisks(secondaryDisks);
        // set machine ID on the diskId
        DiskDescription primDisk =
            primaryDisk.getDiskStatefull() != null ? primaryDisk.getDiskStatefull() : primaryDisk
                .getDiskStandardConf().getDiskStandard();

        primDisk.setDiskID(virtualMachineId);

        CreateVirtualMachine create = new CreateVirtualMachine();
        create.setHypervisorConnection(connection);
        create.setVirtualMahine(virtualMachine);
        return create;
    }

}// create builder
