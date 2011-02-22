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
