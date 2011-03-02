package com.abiquo.tarantino.hypervisor;

import java.net.URL;

import com.abiquo.tarantino.virtualmachine.AbsVirtualMachine;

public interface IHypervisor
{

    // TODO: required default constructor !!!

    /**
     * Connects to the hypervisor.
     * 
     * @param address the url
     * @throws HypervisorException
     */
    public void connect(URL address) throws HypervisorException;

    /**
     * Disconnects from the hypervisor. Currently it's just implemented for the Libvirt based
     * Hypervisors
     * 
     * @param address the url
     * @throws HypervisorException
     */
    public void disconnect() throws HypervisorException;

    /**
     * Gets the address.
     * 
     * @return the address
     */
    public URL getAddress();

    /**
     * Logins in with the user && password.
     * 
     * @param user the user
     * @param password the password
     */
    public void login(String user, String password);

    /**
     * Logs out the hypervisor user
     */
    public void logout();

    /**
     * Creates a new virtual machine.
     * 
     * @param config the config
     * @return the abs virtual machine
     * @throws VirtualMachineException the virtual machine exception
     */
    public AbsVirtualMachine createMachine(VirtualMachineConfiguration config)
        throws VirtualMachineException;

    /**
     * Returns the Hypervisor this class is wrapping.
     * 
     * @return the hypervisor type
     */
    public String getHypervisorType();

    /**
     * Initializes the hypervisor
     * 
     * @param address The hypervisor address
     * @param user the admin user
     * @param password the admin password
     * @throws If initialization fails
     */
    public void init(URL address, String user, String password) throws HypervisorException;

    /**
     * Gets the virtual machine from the hypervisor
     * 
     * @param virtualMachineConfig the virtual machine configuraiton
     * @return the virtual machine
     * @throws HypervisorException
     */
    public AbsVirtualMachine getMachine(VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException;


    
}
