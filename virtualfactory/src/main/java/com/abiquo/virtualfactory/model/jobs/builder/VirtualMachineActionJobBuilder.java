package com.abiquo.virtualfactory.model.jobs.builder;

import com.abiquo.virtualfactory.model.jobs.State;
import com.abiquo.virtualfactory.model.jobs.VirtualMachineAction;

public class VirtualMachineActionJobBuilder extends VirtualFactoryJobBuilder
{

    private String virtualMachineId;

    private State state;

    public VirtualMachineActionJobBuilder connection(String hypervisorID, String hypervisortype,
        String ip, String port, String protocol, String loginUser, String loginPasswoed)
    {
        super
            .connection(hypervisorID, hypervisortype, ip, port, protocol, loginUser, loginPasswoed);
        return this;
    }

    public VirtualMachineActionJobBuilder virtualMachineId(String virtualMachineId)
    {
        this.virtualMachineId = virtualMachineId;

        return this;
    }

    public VirtualMachineActionJobBuilder state(State state)
    {
        this.state = state;

        return this;
    }

    public VirtualMachineAction build()
    {
        VirtualMachineAction vmaction = new VirtualMachineAction();
        vmaction.setHypervisorConnection(connection);
        vmaction.setVirtualMachineID(virtualMachineId);
        vmaction.setState(state);

        return vmaction;

    }

}
