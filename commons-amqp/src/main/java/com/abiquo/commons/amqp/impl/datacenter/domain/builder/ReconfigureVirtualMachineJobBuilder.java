package com.abiquo.commons.amqp.impl.datacenter.domain.builder;

import com.abiquo.commons.amqp.impl.datacenter.domain.HypervisorConnection.HypervisorType;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.dto.ReconfigureVirtualMachineDto;

public class ReconfigureVirtualMachineJobBuilder extends ConfigureVirtualMachineJobBuilder
{

    VirtualMachineDefinition newVmachineDefinition;

    public ReconfigureVirtualMachineJobBuilder connection(HypervisorType hypervisortype, String ip,
        String loginUser, String loginPasswoed)
    {
        super.connection(hypervisortype, ip, loginUser, loginPasswoed);

        return this;
    }
    
    public ReconfigureVirtualMachineJobBuilder setVirtualMachineDefinition(
        VirtualMachineDescriptionBuilder vmBuilder, String virtualMachineId)
    {
        super.setVirtualMachineDefinition(vmBuilder, virtualMachineId);

        return this;
    }

    public ReconfigureVirtualMachineJobBuilder setNewVirtualMachineDefinition(
        VirtualMachineDescriptionBuilder newVmBuilder, String virtualMachineId)
    {
        newVmachineDefinition = newVmBuilder.build(virtualMachineId);

        return this;
    }

    public ReconfigureVirtualMachineDto buildReconfigureVirtualMachineDto()
    {
        ReconfigureVirtualMachineDto reconfigure = new ReconfigureVirtualMachineDto();

        reconfigure.hypervisorConnection = this.connection;
        reconfigure.virtualMachine = this.vmachineDefinition;
        reconfigure.newVirtualMachine = this.newVmachineDefinition;

        return reconfigure;
    }

}
