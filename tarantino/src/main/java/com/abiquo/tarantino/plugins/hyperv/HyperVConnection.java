package com.abiquo.tarantino.plugins.hyperv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.HypervisorConnection;
import com.abiquo.tarantino.errors.VirtualFactoryErrors;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.plugins.hyperv.utils.HyperVConstants;
import com.hyper9.jwbem.SWbemLocator;
import com.hyper9.jwbem.SWbemServices;

public class HyperVConnection implements IHypervisorConnection
{
    
    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(HyperVConnection.class.getName());

    /**
     * The SWbem service for the virtualization namespace.
     */
    private SWbemServices virtService;

    /**
     * The SWbem service for the win32 namespace.
     */
    private SWbemServices cim2Service;

    /**
     * The SWbem service for the wmi namespace.
     */
    private SWbemServices wmiService;

    @Override
    public void login(HypervisorConnection connection) throws VirtualFactoryException
    {

        try
        {
            SWbemLocator loc = new SWbemLocator();
            // address.getHost() ==  ¿?
            virtService =
                loc.connect(connection.getIp(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    connection.getLoginUser(), connection.getLoginPassword());
            cim2Service =
                loc.connect(connection.getIp(), "127.0.0.1", HyperVConstants.CIM_NS, connection.getLoginUser(), connection.getLoginPassword());

        }
        catch (Exception e)
        {
            logger.debug("An error was occurred when connecting to the hypervisor", e);
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION, e.getMessage());
        }


    }

    @Override
    public void logout() throws VirtualFactoryException
    {
        // TODO Auto-generated method stub

    }
    
    /**
     * Gets the virtualization service
     * 
     * @return the virtService
     */
    public SWbemServices getVirtualizationService()
    {
        return virtService;
    }

    /**
     * Gets the Common information model service
     * 
     * @return the cim service
     */
    public SWbemServices getCIMService()
    {
        return cim2Service;
    }

    /**
     * Gets the Windows Management Instrumentation service
     * 
     * @return the wmi service
     */
    public SWbemServices getWMIService(HypervisorConnection connection) throws VirtualFactoryException
    {
        try
        {
            SWbemLocator loc = new SWbemLocator();
            wmiService =
                loc.connect(connection.getIp(), "127.0.0.1", HyperVConstants.WMI_NS, connection.getLoginUser(),
                    connection.getLoginPassword());
        }
        catch (Exception e)
        {
            throw new VirtualFactoryException(VirtualFactoryErrors.HYPERVISOR_CONNECTION, e.getMessage());
        }
        return wmiService;
    }
    
 

}
