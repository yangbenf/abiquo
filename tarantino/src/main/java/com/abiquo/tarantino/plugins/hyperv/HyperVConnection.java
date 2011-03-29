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
package com.abiquo.tarantino.plugins.hyperv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.datacenter.domain.HypervisorConnection;
import com.abiquo.tarantino.errors.VirtualFactoryError;
import com.abiquo.tarantino.errors.VirtualFactoryException;
import com.abiquo.tarantino.hypervisor.IHypervisorConnection;
import com.abiquo.tarantino.plugins.hyperv.utils.HyperVConstants;
import com.hyper9.jwbem.SWbemLocator;
import com.hyper9.jwbem.SWbemServices;

/**
 * 
 * @author destevez (based on the great work of Pedro Navarro)
 *
 */
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
            throw new VirtualFactoryException(VirtualFactoryError.HYPERVISOR_CONNECTION, e.getMessage());
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
            throw new VirtualFactoryException(VirtualFactoryError.HYPERVISOR_CONNECTION, e.getMessage());
        }
        return wmiService;
    }
    
 

}
