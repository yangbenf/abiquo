package com.abiquo.commons.amqp.impl.datacenter.domain.builder;

import com.abiquo.commons.amqp.impl.datacenter.domain.HypervisorConnection.HypervisorType;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.datacenter.domain.dto.ConfigureVirtualMachineDto;

public class ConfigureVirtualMachineJobBuilder extends VirtualFactoryJobBuilder
{

    VirtualMachineDefinition vmachineDefinition;

    public ConfigureVirtualMachineJobBuilder connection(HypervisorType hypervisortype, String ip,
        String loginUser, String loginPasswoed)
    {
        super.connection(hypervisortype, ip, loginUser, loginPasswoed);

        return this;
    }

    public ConfigureVirtualMachineJobBuilder setVirtualMachineDefinition(
        VirtualMachineDescriptionBuilder vmBuilder, String virtualMachineId)
    {
        vmachineDefinition = vmBuilder.build(virtualMachineId);

        return this;
    }

    public ConfigureVirtualMachineDto buildConfigureVirtualMachineDto()
    {
        ConfigureVirtualMachineDto configure = new ConfigureVirtualMachineDto();

        configure.hypervisorConnection = this.connection;
        configure.virtualMachine = this.vmachineDefinition;

        return configure;
    }

}
