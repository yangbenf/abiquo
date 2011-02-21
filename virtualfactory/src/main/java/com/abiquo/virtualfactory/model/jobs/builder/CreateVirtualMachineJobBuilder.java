package com.abiquo.virtualfactory.model.jobs.builder;

import com.abiquo.virtualfactory.model.jobs.AuxDisk;
import com.abiquo.virtualfactory.model.jobs.CreateVirtualMachine;
import com.abiquo.virtualfactory.model.jobs.DiskDesc;
import com.abiquo.virtualfactory.model.jobs.DiskStandard;
import com.abiquo.virtualfactory.model.jobs.DiskStatefull;
import com.abiquo.virtualfactory.model.jobs.HardwareConf;
import com.abiquo.virtualfactory.model.jobs.VirtualMachine;
import com.abiquo.virtualfactory.model.jobs.VirtualMachine.NetworkConf;
import com.abiquo.virtualfactory.model.jobs.VirtualMachine.PrimaryDisk;
import com.abiquo.virtualfactory.model.jobs.VirtualMachine.PrimaryDisk.DiskStandardConf;
import com.abiquo.virtualfactory.model.jobs.VirtualMachine.SecondaryDisks;
import com.abiquo.virtualfactory.model.jobs.VirtualNIC;

public class CreateVirtualMachineJobBuilder extends VirtualFactoryJobBuilder
{
    private HardwareConf hardConf;

    private NetworkConf netConf;

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
        hardConf = new HardwareConf();
        hardConf.setVirtualCpu(virtualCpu);
        hardConf.setRamInMb(ramInMb);

        return this;
    }

    public CreateVirtualMachineJobBuilder addNetwork(String vSwitchName, String macAddress,
        String networkName, String vlanTag, String sequence)
    {
        if (netConf == null)
        {
            netConf = new NetworkConf();
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

        VirtualMachine.PrimaryDisk.DiskStandardConf standard = new DiskStandardConf();
        standard.setDiskStandard(disk);
        standard.setDestinationDatastore(destinationDatastore);

        primaryDisk = new PrimaryDisk();
        primaryDisk.setDiskStandardConf(standard);
        return this;
    }

    public CreateVirtualMachineJobBuilder primaryDisk(String format, String capacity, String iqn)
    {
        DiskStatefull disk = new DiskStatefull();
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

        AuxDisk auxDisk = new AuxDisk();
        // auxDisk.setDiskID(value); // XXX unset
        auxDisk.setFormat(format);
        auxDisk.setCapacity(capacity);
        auxDisk.setIqn(iqn);

        secondaryDisks.getAuxDisk().add(auxDisk);

        return this;
    }

    public CreateVirtualMachine build(String virtualMachineId)
    {
        VirtualMachine virtualMachine = new VirtualMachine();
        // TODO check not null
        virtualMachine.setMachineID(virtualMachineId);
        virtualMachine.setHardwareConf(hardConf);
        virtualMachine.setNetworkConf(netConf);
        virtualMachine.setPrimaryDisk(primaryDisk);
        virtualMachine.setSecondaryDisks(secondaryDisks);
        // set machine ID on the diskId
        DiskDesc primDisk =
            primaryDisk.getDiskStatefull() != null ? primaryDisk.getDiskStatefull() : primaryDisk
                .getDiskStandardConf().getDiskStandard();

        primDisk.setDiskID(virtualMachineId);

        CreateVirtualMachine create = new CreateVirtualMachine();
        create.setHypervisorConnection(connection);
        create.setVirtualMahine(virtualMachine);
        return create;
    }

}// create builder
