package com.abiquo.commons.amqp.impl.datacenter.domain;

public class DHCPRule
{
    protected String ip;

    protected String macAddress;

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        // TODO validate
        this.ip = ip;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        // TODO validate
        this.macAddress = macAddress;
    }
}
