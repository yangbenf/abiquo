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

package com.abiquo.tarantino.plugins.esxi;

import java.util.LinkedList;
import java.util.List;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.tarantino.errors.VirtualFactoryError;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.utils.AddressingUtils;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;

/**
 * Achieve communications to ESXi 3.5 VMWare bare metal hypervisor using the Java/Axis SDK bindings.
 * TODO: check getVms, no multiple VM for a single VmwareMachine instance
 * 
 * @author pnavarro, apuig
 */
public class VmwareMachine extends AbsVmwareMachine
{

    public VmwareMachine(com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition vmdef,
        VmwareHypervisorConnection hypervisor)
    {
        super(vmdef, hypervisor);
    }

    /**
     * Used during creation sets the additional configuration into the VM.
     * 
     * @param computerResMOR, the computer resource related to the current VM.
     * @param hostMOR, the host related to the current VM.
     * @return a configuration containing the specified resources
     */
    @Override
    public VirtualMachineConfigSpec configureVM(ManagedObjectReference computerResMOR,
        ManagedObjectReference hostMOR) throws VirtualFactoryException
    {
        VirtualMachineConfigSpec vmConfigSpec;

        try
        {

            // TODO #createVMConfigSpec defines not convenient default data, change this
            final String vmName = vmdef.getMachineID();

            // TODO check is a standard disk
            // TODO use the datastore name instead of mount point
            final String datastoreName =
                vmdef.getPrimaryDisk().getDiskStandardConf().getDestinationDatastore();
            final long diskSize = vmdef.getPrimaryDisk().getDiskStandardConf().getDiskStandard()
                    .getCapacityInBytes();

            vmConfigSpec =
                hypervisor
                    .getUtils()
                    .getUtilBasics()
                    .createVmConfigSpec(vmName, datastoreName, diskSize, computerResMOR, hostMOR,
                        null);

            List<VirtualDeviceConfigSpec> nicSpecList =
                hypervisor.getUtils().getUtilNetwork()
                    .configureNetworkInterfaces(vmdef.getNetworkConfiguration().getVirtualNIC());
            vmConfigSpec = addDeviceSpecs(vmConfigSpec, nicSpecList);

            final long rammb = vmdef.getHardwareConfiguration().getRamInMb();
            final int cpu = vmdef.getHardwareConfiguration().getNumVirtualCpus();

            String guestId = hypervisor.getUtils().getSessionOption("guestosid");

            vmConfigSpec.setName(vmdef.getMachineID());
            vmConfigSpec.setAnnotation("VirtualMachine Annotation");
            vmConfigSpec.setMemoryMB(rammb);// config.getMemoryRAM() / 1048576);
            vmConfigSpec.setNumCPUs(cpu);// config.getCpuNumber());
            vmConfigSpec.setGuestId(guestId);

            final int rdport = vmdef.getNetworkConfiguration().getRdPort();

            if (AddressingUtils.isValidPort(String.valueOf(rdport)))
            {
                OptionValue vncEnabled = new OptionValue();
                vncEnabled.setKey("RemoteDisplay.vnc.enabled");
                vncEnabled.setValue("true");

                OptionValue vncPort = new OptionValue();
                vncPort.setKey("RemoteDisplay.vnc.port");
                vncPort.setValue(rdport);

                OptionValue[] values = new OptionValue[] {vncEnabled, vncPort};

                vmConfigSpec.setExtraConfig(values);
            }

        }
        catch (VirtualFactoryException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryError.CONFIG, String.format(
                "Virtual Machine : %s\nCaused by:%s", vmdef.getMachineID(), e.toString()));
        }

        return vmConfigSpec;
    }

    private VirtualMachineConfigSpec addDeviceSpecs(VirtualMachineConfigSpec vmConfigSpec,
        List<VirtualDeviceConfigSpec> nicSpecList)
    {
        List<VirtualDeviceConfigSpec> allspecs = new LinkedList<VirtualDeviceConfigSpec>();
        for (VirtualDeviceConfigSpec spec : vmConfigSpec.getDeviceChange())
        {
            allspecs.add(spec);
        }
        allspecs.addAll(nicSpecList);
        vmConfigSpec.setDeviceChange(allspecs.toArray(new VirtualDeviceConfigSpec[] {}));

        return vmConfigSpec;
    }

   
 

}
