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

package com.abiquo.commons.amqp.impl.datacenter.domain.builder;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.abiquo.commons.amqp.impl.datacenter.domain.State;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.CreateVirtualMachine;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.ObjectFactory;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.SnapshotVirtualMachine;
import com.abiquo.commons.amqp.impl.datacenter.domain.jobs.VirtualMachineAction;

public class VirtualFactoryTestJobs
{

    public CreateVirtualMachine testCreateVirtualMachine()
    {

        return new CreateVirtualMachineJobBuilder()
            .connection("hypervisorID", "XEN", "10.60.1.15", "78889", "https", "root", "root")
            .hardware(1, 256)
            .addNetwork("vSwitchName", "macAddress", "networkName", "vlanTag", "1")
            // .primaryDisk("RAW", "1024", "iqn.bla.bla-lun-0")
            .primaryDisk("RAW", "1024", "nfs-devel:/opt/vm_repo", "1/rs.bcn/m0n0/m0n0.iso",
                "datastore1")

            .addAuxDisk("RAW", "1024", "iqn....", 1).build("virtualMachineID");

    }

    public VirtualMachineAction testVirtualMachineAction()
    {
        return new VirtualMachineActionJobBuilder()
            .connection("hypervisorID", "XEN", "10.60.1.15", "78889", "https", "root", "root")
            .virtualMachineId("virtualMachineId").state(State.PAUSED).build();
    }

    public SnapshotVirtualMachine testSnapshoo()
    {
        return new SnapshotVirtualMachineJobBuilder()
            .connection("hypervisorID", "XEN", "10.60.1.15", "78889", "https", "root", "root")
            .source("virtualMachineId", "RAW", "1024", "datastore1", "virtualMachineId")
            .destination("RAW", "1024", "nfs-devel:/opt/vm_repository", "1/some/bundle/m0n0.iso")
            .build();
    }

    /**
     * Serialization issues
     */
    private ObjectFactory jobsObjectF;

    private JAXBContext context;

    public VirtualFactoryTestJobs() throws JAXBException
    {
        jobsObjectF = new ObjectFactory();
        context =
            JAXBContext.newInstance(CreateVirtualMachine.class, SnapshotVirtualMachine.class,
                VirtualMachineAction.class);
    }

    public String serialize(Object any) throws JAXBException
    {

        Marshaller marshall;
        // XMLStreamWriter writer =
        // Stax2Factory.getStreamWriterFactory().createXMLStreamWriter(os);
        StringWriter swriter = new StringWriter();

        marshall = context.createMarshaller();
        // marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));

        marshall.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // marshall.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, Boolean.TRUE);

        if (any instanceof CreateVirtualMachine)
        {
            marshall.marshal(jobsObjectF.createCreateVirtualMachine((CreateVirtualMachine) any),
                swriter);
        }
        else if (any instanceof SnapshotVirtualMachine)
        {
            marshall.marshal(
                jobsObjectF.createSnapshootVirtualMachine((SnapshotVirtualMachine) any), swriter);
        }
        else if (any instanceof VirtualMachineAction)
        {
            marshall.marshal(jobsObjectF.createVirtualMachineAction((VirtualMachineAction) any),
                swriter);
        }

        return swriter.toString();

    }

    public static void main(String[] args) throws Exception
    {
        VirtualFactoryTestJobs testBuidler = new VirtualFactoryTestJobs();

        System.err.println(testBuidler.serialize(testBuidler.testVirtualMachineAction()));
        // System.err.println(testBuidler.serialize(testBuidler.testSnapshoo()));
        // System.err.println(testBuidler.serialize(testBuidler.testCreateVirtualMachine()));

    }

}
