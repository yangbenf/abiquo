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

import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.SnapshootVirtualMachine;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.VirtualMachineDefinitionDto;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;

public interface IVirtualMachine
{
	boolean exist(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
	
    State getState(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);

    /**
     * Creates a new virtual machine instance
     */
    void doConfigure(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    /**
     * Destroy the virtual machine
     */
    void doDeconfigure(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);  
    
    void doPowerOn(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    void doPowerOff(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    void doReset(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    void doPause(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    void doResume(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition);
    
    /**
     * Copy disk 
     */
    void doSnapshot(IHypervisorConnection connection, VirtualMachineDefinitionDto vmdefinition, DiskStandard destinationDisk);
 
    
    void reconfigure(IHypervisorConnection connection, VirtualMachineDefinitionDto currentvmachine,VirtualMachineDefinitionDto newvmachine); // lo que deixem cambiar

    
    // public VirtualMachineDefinitionDto getVirtualMachine();
}
