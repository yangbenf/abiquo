package com.abiquo.commons.amqp.impl.datacenter.domain.jobs;

import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.CONFIGURE;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.DECONFIGURE;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.PAUSE;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.POWEROFF;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.POWERON;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.RESET;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.RESUME;
import static com.abiquo.commons.amqp.impl.datacenter.domain.jobs.StateTransaction.SNAPSHOT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class StateTransactionTest
{
    @Test
    public void test_configure()
    {
        assertEquals(CONFIGURE.getEndState(), State.CONFIGURED);
        assertTrue(CONFIGURE.isValidOrigin(State.UNDEPLOYED));
        assertFalse(CONFIGURE.isValidOrigin(State.ON));
        assertFalse(CONFIGURE.isValidOrigin(State.OFF));
        assertFalse(CONFIGURE.isValidOrigin(State.UNKNOWN));
        assertFalse(CONFIGURE.isValidOrigin(State.PAUSED));
        assertFalse(CONFIGURE.isValidOrigin(State.CONFIGURED));
    }

    @Test
    public void test_deconfigure()
    {
        assertEquals(DECONFIGURE.getEndState(), State.UNDEPLOYED);
        assertTrue(DECONFIGURE.isValidOrigin(State.CONFIGURED));
        assertFalse(DECONFIGURE.isValidOrigin(State.ON));
        assertFalse(DECONFIGURE.isValidOrigin(State.OFF));
        assertFalse(DECONFIGURE.isValidOrigin(State.UNDEPLOYED));
        assertFalse(DECONFIGURE.isValidOrigin(State.UNKNOWN));
        assertFalse(DECONFIGURE.isValidOrigin(State.PAUSED));
    }

    @Test
    public void test_powerOn()
    {
        assertEquals(POWERON.getEndState(), State.ON);
        assertTrue(POWERON.isValidOrigin(State.OFF));
        assertTrue(POWERON.isValidOrigin(State.CONFIGURED));
        assertFalse(POWERON.isValidOrigin(State.ON));
        assertFalse(POWERON.isValidOrigin(State.UNDEPLOYED));
        assertFalse(POWERON.isValidOrigin(State.UNKNOWN));
        assertFalse(POWERON.isValidOrigin(State.PAUSED));
    }

    @Test
    public void test_powerOff()
    {
        assertEquals(POWEROFF.getEndState(), State.OFF);
        assertTrue(POWEROFF.isValidOrigin(State.ON));
        assertFalse(POWEROFF.isValidOrigin(State.OFF));
        assertFalse(POWEROFF.isValidOrigin(State.UNDEPLOYED));
        assertFalse(POWEROFF.isValidOrigin(State.UNKNOWN));
        assertFalse(POWEROFF.isValidOrigin(State.PAUSED));
        assertFalse(POWEROFF.isValidOrigin(State.CONFIGURED));
    }

    @Test
    public void test_reset()
    {
        assertEquals(RESET.getEndState(), State.ON);
        assertTrue(RESET.isValidOrigin(State.ON));
        assertFalse(RESET.isValidOrigin(State.OFF));
        assertFalse(RESET.isValidOrigin(State.UNDEPLOYED));
        assertFalse(RESET.isValidOrigin(State.UNKNOWN));
        assertFalse(RESET.isValidOrigin(State.PAUSED));
        assertFalse(RESET.isValidOrigin(State.CONFIGURED));
    }

    @Test
    public void test_pause()
    {
        assertEquals(PAUSE.getEndState(), State.PAUSED);
        assertTrue(PAUSE.isValidOrigin(State.ON));
        assertFalse(PAUSE.isValidOrigin(State.OFF));
        assertFalse(PAUSE.isValidOrigin(State.UNDEPLOYED));
        assertFalse(PAUSE.isValidOrigin(State.UNKNOWN));
        assertFalse(PAUSE.isValidOrigin(State.PAUSED));
        assertFalse(PAUSE.isValidOrigin(State.CONFIGURED));
    }

    @Test
    public void test_resume()
    {
        assertEquals(RESUME.getEndState(), State.ON);
        assertTrue(RESUME.isValidOrigin(State.PAUSED));
        assertFalse(RESUME.isValidOrigin(State.ON));
        assertFalse(RESUME.isValidOrigin(State.OFF));
        assertFalse(RESUME.isValidOrigin(State.UNDEPLOYED));
        assertFalse(RESUME.isValidOrigin(State.UNKNOWN));
        assertFalse(RESUME.isValidOrigin(State.CONFIGURED));
    }

    @Test
    public void test_snapshot()
    {
        assertEquals(SNAPSHOT.getEndState(), State.OFF);
        assertTrue(SNAPSHOT.isValidOrigin(State.OFF));
        assertFalse(SNAPSHOT.isValidOrigin(State.ON));
        assertFalse(SNAPSHOT.isValidOrigin(State.UNDEPLOYED));
        assertFalse(SNAPSHOT.isValidOrigin(State.UNKNOWN));
        assertFalse(SNAPSHOT.isValidOrigin(State.PAUSED));
        assertFalse(SNAPSHOT.isValidOrigin(State.CONFIGURED));
    }
}
