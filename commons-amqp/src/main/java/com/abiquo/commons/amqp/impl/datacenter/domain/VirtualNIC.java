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

package com.abiquo.commons.amqp.impl.datacenter.domain;

public class VirtualNIC extends DHCPRule
{
    protected String vSwitchName;

    protected String networkName;

    protected int vlanTag;

    protected String forwardMode;

    protected String netAddress;

    protected String gateway;

    protected String mask;

    protected String primaryDNS;

    protected String secondaryDNS;

    protected String sufixDNS;

    protected int sequence;

    public String getVSwitchName()
    {
        return vSwitchName;
    }

    public void setVSwitchName(String value)
    {
        this.vSwitchName = value;
    }

    public String getNetworkName()
    {
        return networkName;
    }

    public void setNetworkName(String value)
    {
        this.networkName = value;
    }

    public int getVlanTag()
    {
        return vlanTag;
    }

    public void setVlanTag(int value)
    {
        this.vlanTag = value;
    }

    public String getForwardMode()
    {
        return forwardMode;
    }

    public void setForwardMode(String value)
    {
        this.forwardMode = value;
    }

    public String getNetAddress()
    {
        return netAddress;
    }

    public void setNetAddress(String value)
    {
        this.netAddress = value;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String value)
    {
        this.gateway = value;
    }

    public String getMask()
    {
        return mask;
    }

    public void setMask(String value)
    {
        this.mask = value;
    }

    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(String value)
    {
        this.primaryDNS = value;
    }

    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(String value)
    {
        this.secondaryDNS = value;
    }

    public String getSufixDNS()
    {
        return sufixDNS;
    }

    public void setSufixDNS(String value)
    {
        this.sufixDNS = value;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int value)
    {
        this.sequence = value;
    }
}
