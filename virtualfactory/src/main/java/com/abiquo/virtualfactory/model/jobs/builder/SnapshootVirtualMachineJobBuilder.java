package com.abiquo.virtualfactory.model.jobs.builder;

import com.abiquo.virtualfactory.model.jobs.DiskStandard;
import com.abiquo.virtualfactory.model.jobs.DiskStatefull;
import com.abiquo.virtualfactory.model.jobs.SnapshootVirtualMachine;
import com.abiquo.virtualfactory.model.jobs.SnapshootVirtualMachine.SourceDisk;

public class SnapshootVirtualMachineJobBuilder extends VirtualFactoryJobBuilder
{

    private SourceDisk source;

    private DiskStandard destination;

    public SnapshootVirtualMachineJobBuilder connection(String hypervisorID, String hypervisortype,
        String ip, String port, String protocol, String loginUser, String loginPasswoed)
    {
        super
            .connection(hypervisorID, hypervisortype, ip, port, protocol, loginUser, loginPasswoed);
        return this;
    }

    public SnapshootVirtualMachineJobBuilder source(String virtualMachineId, String format,
        String capacity, String datastore, String path)
    {

        source = new SourceDisk();

        DiskStandard disk = new DiskStandard();
        disk.setDiskID(virtualMachineId);
        disk.setFormat(format);
        disk.setCapacity(capacity);
        disk.setDatastore(datastore);
        disk.setPath(path);

        source.setDiskStandard(disk);

        return this;
    }

    public SnapshootVirtualMachineJobBuilder source(String virtualMachineId, String format,
        String capacity, String iqn)
    {

        source = new SourceDisk();

        DiskStatefull disk = new DiskStatefull();
        disk.setDiskID(virtualMachineId);
        disk.setFormat(format);
        disk.setCapacity(capacity);
        disk.setIqn(iqn);

        source.setDiskStatefull(disk);

        return this;
    }

    public SnapshootVirtualMachineJobBuilder destination(String format, String capacity,
        String datastore, String path)
    {

        destination = new DiskStandard();

        destination.setFormat(format);
        destination.setCapacity(capacity);
        destination.setDatastore(datastore);
        destination.setPath(path);

        return this;
    }

    public SnapshootVirtualMachine build()
    {
        SnapshootVirtualMachine sn = new SnapshootVirtualMachine();
        sn.setHypervisorConnection(connection);
        sn.setSourceDisk(source);
        sn.setDestinationDisk(destination);
        return sn;
    }

}
