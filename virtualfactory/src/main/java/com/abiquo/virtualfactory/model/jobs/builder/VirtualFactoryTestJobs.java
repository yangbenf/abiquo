package com.abiquo.virtualfactory.model.jobs.builder;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.virtualfactory.model.jobs.CreateVirtualMachine;
import com.abiquo.virtualfactory.model.jobs.ObjectFactory;
import com.abiquo.virtualfactory.model.jobs.SnapshootVirtualMachine;

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

    public SnapshootVirtualMachine testSnapshoo()
    {
        return new SnapshootVirtualMachineJobBuilder()
            .connection("hypervisorID", "XEN", "10.60.1.15", "78889", "https", "root", "root")
            .source("virtualMachineId", "RAW", "1024", "datastore1", "virtualMachineId")
            .destination("RAW", "1024", "nfs-devel:/opt/vm_repository", "1/some/bundle/m0n0.iso")
            .build();
    }

    private ObjectFactory jobsObjectF;

    private JAXBContext context;

    public VirtualFactoryTestJobs() throws JAXBException
    {
        jobsObjectF = new ObjectFactory();
        context =
            JAXBContext.newInstance(CreateVirtualMachine.class, SnapshootVirtualMachine.class);
    }

    public String serialize(Object any) throws XMLException
    {

        Marshaller marshall;

        try
        {
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
                marshall.marshal(
                    jobsObjectF.createCreateVirtualMachine((CreateVirtualMachine) any), swriter);
            }
            else if (any instanceof SnapshootVirtualMachine)
            {
                marshall.marshal(
                    jobsObjectF.createSnapshootVirtualMachine((SnapshootVirtualMachine) any),
                    swriter);
            }

            return swriter.toString();
        }
        catch (JAXBException ea)
        {
            throw new XMLException(ea);
        }
    }

    public static void main(String[] args) throws Exception
    {
        VirtualFactoryTestJobs testBuidler = new VirtualFactoryTestJobs();

        System.err.println(testBuidler.serialize(testBuidler.testSnapshoo()));
        //System.err.println(testBuidler.serialize(testBuidler.testCreateVirtualMachine()));

    }

}
