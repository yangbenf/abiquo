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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.abiquo.virtualfactory.context.ApplicationContextProvider;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.model.IHypervisor;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:spring/applicationcontext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class HypervisorManagerTest {

	@Test
	public void loadPlugins()
	{
		HypervisorManager manager = getManager();
		
		Assert.assertTrue(manager.getHypervisors().size() > 0);
	}
	
	@Test
	public void getHypervisorPluginByType() throws Exception
	{
		HypervisorManager manager = getManager();
		IHypervisor hypervisor = manager.instantiateHypervisor("VMX-04");
		
		Assert.assertNotNull(hypervisor);
	}
	
	@Test(expected = PluginException.class)
	public void getHypervisorPluginByMissingType() throws Exception
	{
		String type = "VMX-0404";
		HypervisorManager manager = getManager();
		manager.instantiateHypervisor(type);
		
		Assert.fail("found hypervisor for type " + type + " but it should be missed");
	}

	private HypervisorManager getManager() {
		HypervisorManager manager = ApplicationContextProvider.getApplicationContext().getBean(HypervisorManager.class);
		return manager;
	}		
}
