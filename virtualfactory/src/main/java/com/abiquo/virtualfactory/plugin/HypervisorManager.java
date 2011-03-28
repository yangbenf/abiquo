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

package com.abiquo.virtualfactory.plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.virtualfactory.context.ApplicationContextProvider;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.hypervisor.Hypervisor;
import com.abiquo.virtualfactory.model.IHypervisor;

public class HypervisorManager
{

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(HypervisorManager.class);

    private Map<HypervisorType, IHypervisor> hypervisors = new HashMap<HypervisorType, IHypervisor>();   

    public void loadPlugins()
    {
        logger.info("Adding hypervisors: ");

        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(Hypervisor.class);
                
        for (Object hypervisor : beans.values())
        {
        	if (!(hypervisor instanceof IHypervisor))
        	{
        		logger.warn("!!! ignoring hypervisor that doesn't match IHypervisor: " + hypervisor.getClass());
        		continue;
        	}
        	
        	Hypervisor annotation = hypervisor.getClass().getAnnotation(Hypervisor.class);
        	if (!hypervisors.containsKey(annotation.type()) || annotation.overrides())
        	{
        		hypervisors.put(annotation.type(), (IHypervisor) hypervisor);
        	}
        }
    }

    /**
     * Gets the singleton instance . Or creates a new instance.
     * 
     * @param user the admin user
     * @param password the admin password
     * @throws HypervisorException
     */
    public IHypervisor getHypervisor(String type, URL address, String user, String password)
        throws PluginException, HypervisorException
    {
        IHypervisor hyper;

        hyper = instantiateHypervisor(type);

        if (address == null)
        {
            throw new HypervisorException("The url to connect to the hypervisor can not be null");
        }

        hyper.init(address, user, password);

        hyper.connect(address);

        hyper.logout();

        return hyper;
    }

    /**
     * Creates a new Hypervisor plugin.
     * 
     * @param type the desired hypervisor type
     * @return new plugin instance for the given hypervisor type.
     * @throws PluginException if there is not any class implementing the desired hypervisor type or
     *             exist but can not no be instantiated (not default empty constructor ?)
     * @see IHypervisor.getHypervisorType()
     */
    protected IHypervisor instantiateHypervisor(String type) throws PluginException
    {
    	try
    	{
	    	HypervisorType hType = HypervisorType.fromValue(type);
	        IHypervisor hypervisor = hypervisors.get(hType);
	        
	        if (hypervisor == null) {
	        	throw new PluginException("Hypervisor not found for type: " + type);
	        }
	        
	        return hypervisor;
    	} catch (IllegalArgumentException e)
    	{
    		throw new PluginException("Hypervisor not found for type: " + type);
    	}
    }
    
    // used by tests
    protected Map<HypervisorType, IHypervisor> getHypervisors()
    {
    	return hypervisors;
    }

}
