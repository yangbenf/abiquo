package com.abiquo.commons.amqp.impl.datacenter.domain.jobs;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class StateTest
{
    @Test(expectedExceptions = RuntimeException.class)
    public void test_invalidTravel()
    {
        State.UNKNOWN.travel(StateTransaction.CONFIGURE);
    }

    @Test
    public void test_configureTravel()
    {
        assertEquals(State.UNDEPLOYED.travel(StateTransaction.CONFIGURE), State.CONFIGURED);
    }

    @Test
    public void test_deconfigureTravel()
    {
        assertEquals(State.CONFIGURED.travel(StateTransaction.DECONFIGURE), State.UNDEPLOYED);
    }

    @Test
    public void test_powerOnTravel()
    {
        assertEquals(State.OFF.travel(StateTransaction.POWERON), State.ON);
        assertEquals(State.CONFIGURED.travel(StateTransaction.POWERON), State.ON);
    }

    @Test
    public void test_powerOffTravel()
    {
        assertEquals(State.ON.travel(StateTransaction.POWEROFF), State.OFF);
    }

    @Test
    public void test_resetTravel()
    {
        assertEquals(State.ON.travel(StateTransaction.RESET), State.ON);
    }

    @Test
    public void test_pauseTravel()
    {
        assertEquals(State.ON.travel(StateTransaction.PAUSE), State.PAUSED);
    }

    @Test
    public void test_resumeTravel()
    {
        assertEquals(State.PAUSED.travel(StateTransaction.RESUME), State.ON);
    }

    @Test
    public void test_snapshotTravel()
    {
        assertEquals(State.OFF.travel(StateTransaction.SNAPSHOT), State.OFF);
    }
}
