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

package com.abiquo.commons.amqp.impl.datacenter.domain.jobs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VirtualNIC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VirtualNIC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vSwitchName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="macAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="networkName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vlanTag" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="leaseName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forwardMode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="netAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gateway" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mask" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="primaryDNS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="secondaryDNS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sufixDNS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VirtualNIC", propOrder = {
    "vSwitchName",
    "macAddress",
    "networkName",
    "vlanTag",
    "leaseName",
    "forwardMode",
    "ip",
    "netAddress",
    "gateway",
    "mask",
    "primaryDNS",
    "secondaryDNS",
    "sufixDNS",
    "sequence"
})
public class VirtualNIC {

    @XmlElement(required = true)
    protected String vSwitchName;
    @XmlElement(required = true)
    protected String macAddress;
    @XmlElement(required = true)
    protected String networkName;
    @XmlElement(required = true)
    protected String vlanTag;
    @XmlElement(required = true)
    protected String leaseName;
    @XmlElement(required = true)
    protected String forwardMode;
    @XmlElement(required = true)
    protected String ip;
    @XmlElement(required = true)
    protected String netAddress;
    @XmlElement(required = true)
    protected String gateway;
    @XmlElement(required = true)
    protected String mask;
    @XmlElement(required = true)
    protected String primaryDNS;
    @XmlElement(required = true)
    protected String secondaryDNS;
    @XmlElement(required = true)
    protected String sufixDNS;
    @XmlElement(required = true)
    protected String sequence;

    /**
     * Gets the value of the vSwitchName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVSwitchName() {
        return vSwitchName;
    }

    /**
     * Sets the value of the vSwitchName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVSwitchName(String value) {
        this.vSwitchName = value;
    }

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the networkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Sets the value of the networkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetworkName(String value) {
        this.networkName = value;
    }

    /**
     * Gets the value of the vlanTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVlanTag() {
        return vlanTag;
    }

    /**
     * Sets the value of the vlanTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVlanTag(String value) {
        this.vlanTag = value;
    }

    /**
     * Gets the value of the leaseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeaseName() {
        return leaseName;
    }

    /**
     * Sets the value of the leaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeaseName(String value) {
        this.leaseName = value;
    }

    /**
     * Gets the value of the forwardMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForwardMode() {
        return forwardMode;
    }

    /**
     * Sets the value of the forwardMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForwardMode(String value) {
        this.forwardMode = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIp(String value) {
        this.ip = value;
    }

    /**
     * Gets the value of the netAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetAddress() {
        return netAddress;
    }

    /**
     * Sets the value of the netAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetAddress(String value) {
        this.netAddress = value;
    }

    /**
     * Gets the value of the gateway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Sets the value of the gateway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGateway(String value) {
        this.gateway = value;
    }

    /**
     * Gets the value of the mask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMask(String value) {
        this.mask = value;
    }

    /**
     * Gets the value of the primaryDNS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryDNS() {
        return primaryDNS;
    }

    /**
     * Sets the value of the primaryDNS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryDNS(String value) {
        this.primaryDNS = value;
    }

    /**
     * Gets the value of the secondaryDNS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryDNS() {
        return secondaryDNS;
    }

    /**
     * Sets the value of the secondaryDNS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryDNS(String value) {
        this.secondaryDNS = value;
    }

    /**
     * Gets the value of the sufixDNS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSufixDNS() {
        return sufixDNS;
    }

    /**
     * Sets the value of the sufixDNS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSufixDNS(String value) {
        this.sufixDNS = value;
    }

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequence(String value) {
        this.sequence = value;
    }

}
