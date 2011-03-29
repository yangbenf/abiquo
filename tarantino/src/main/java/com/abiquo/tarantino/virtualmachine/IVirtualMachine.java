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

package com.abiquo.tarantino.virtualmachine;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;

public interface IVirtualMachine
{
    boolean exist(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    State getState(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    /**
     * Creates a new virtual machine instance
     */
    void doConfigure(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    /**
     * Destroy the virtual machine
     */
    void doDeconfigure(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    void doPowerOn(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    void doPowerOff(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    void doReset(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    void doPause(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    void doResume(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition);

    /**
     * Copy disk
     */
    void doSnapshot(IHypervisorConnection connection, VirtualMachineDefinition vmdefinition,
        DiskStandard destinationDisk);

    void reconfigure(IHypervisorConnection connection, VirtualMachineDefinition currentvmachine,
        VirtualMachineDefinition newvmachine); // lo que deixem cambiar

    // public VirtualMachineDefinitionDto getVirtualMachine();
}
