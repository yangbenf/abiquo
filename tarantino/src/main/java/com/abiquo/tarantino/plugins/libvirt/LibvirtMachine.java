package com.abiquo.tarantino.plugins.libvirt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.DiskStandard;
import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.VirtualMachineDefinition;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.virtualmachine.IVirtualMachine;

public class LibvirtMachine implements IVirtualMachine<LibvirtConnection>
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(LibvirtMachine.class);

    @Override
    public boolean exist(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public State getState(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doConfigure(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doDeconfigure(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doPowerOn(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doPowerOff(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doReset(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void doPause(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doResume(LibvirtConnection connection, VirtualMachineDefinition vmdefinition)
        throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void doSnapshot(LibvirtConnection connection, VirtualMachineDefinition vmdefinition,
        DiskStandard destinationDisk) throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void reconfigure(LibvirtConnection connection, VirtualMachineDefinition currentvmachine,
        VirtualMachineDefinition newvmachine) throws VirtualFactoryException
    {
        // TODO Auto-generated method stub
    }
}
