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
