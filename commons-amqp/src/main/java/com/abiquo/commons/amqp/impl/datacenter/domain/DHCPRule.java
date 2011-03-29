package com.abiquo.commons.amqp.impl.datacenter.domain;

public class DHCPRule
{
    protected String ip;

    protected String macAddress;

    protected String leaseName;

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

    public String getLeaseName()
    {
        return leaseName;
    }

    public void setLeaseName(String leaseName)
    {
        this.leaseName = leaseName;
    }
}
