package com.abiquo.tarantino.utils;

import java.util.List;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskDescription;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualNIC;

/**
 * TODO unimplemented
 */
public class ReconfigureUtils
{

    /**
     * @return 0, if any change, a positive integer indicate the ram increase.
     */
    public static int getRamInMbDiff(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        return newVm.getHardwareConfiguration().getRamInMb()
            - currentVm.getHardwareConfiguration().getRamInMb();
    }

    /**
     * @return 0, if any change, a positive integer indicate the ram increase.
     */
    public static int getNumVirtualCpuDiff(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        return newVm.getHardwareConfiguration().getNumVirtualCpus()
            - currentVm.getHardwareConfiguration().getNumVirtualCpus();
    }

    /**
     * @return true it the newVm have change the rdPort
     */
    public static boolean isRdPortChanged(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        return newVm.getNetworkConfiguration().getRdPort() != currentVm.getNetworkConfiguration()
            .getRdPort();
    }

    public static List<VirtualNIC> getRemovedVirtualNICs(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        throw new RuntimeException("unimplemented");
    }

    public static List<VirtualNIC> getAddedVirtualNICs(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        throw new RuntimeException("unimplemented");
    }

    public static List<DiskDescription> getRemovedAuxDisks(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        throw new RuntimeException("unimplemented");
    }

    public static List<DiskDescription> getAddedAuxDisks(VirtualMachineDefinition currentVm,
        VirtualMachineDefinition newVm)
    {
        throw new RuntimeException("unimplemented");
    }
}
