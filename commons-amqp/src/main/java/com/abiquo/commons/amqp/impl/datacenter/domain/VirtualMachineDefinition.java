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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class VirtualMachineDefinition
{
    protected String machineUUID;

    protected String machineName;

    protected HardwareConfiguration hardwareConfiguration;

    protected NetworkConfiguration networkConfiguration;

    protected PrimaryDisk primaryDisk;

    protected SecondaryDisks secondaryDisks;

    public String getMachineUUID()
    {
        return machineUUID;
    }

    public void setMachineUUID(String machineUUID)
    {
        this.machineUUID = machineUUID;
    }

    public String getMachineName()
    {
        return machineName;
    }

    public void setMachineName(String machineName)
    {
        this.machineName = machineName;
    }

    public HardwareConfiguration getHardwareConfiguration()
    {
        return hardwareConfiguration;
    }

    public void setHardwareConfiguration(HardwareConfiguration hardwareConfiguration)
    {
        this.hardwareConfiguration = hardwareConfiguration;
    }

    public NetworkConfiguration getNetworkConfiguration()
    {
        return networkConfiguration;
    }

    public void setNetworkConfiguration(NetworkConfiguration networkConfiguration)
    {
        this.networkConfiguration = networkConfiguration;
    }

    public PrimaryDisk getPrimaryDisk()
    {
        return primaryDisk;
    }

    public void setPrimaryDisk(PrimaryDisk primaryDisk)
    {
        this.primaryDisk = primaryDisk;
    }

    public SecondaryDisks getSecondaryDisks()
    {
        return secondaryDisks;
    }

    public void setSecondaryDisks(SecondaryDisks secondaryDisks)
    {
        this.secondaryDisks = secondaryDisks;
    }

    public static class HardwareConfiguration
    {
        protected int numVirtualCpus;

        protected int ramInMb;

        public int getNumVirtualCpus()
        {
            return numVirtualCpus;
        }

        public void setNumVirtualCpus(int numVirtualCpus)
        {
            this.numVirtualCpus = numVirtualCpus;
        }

        public int getRamInMb()
        {
            return ramInMb;
        }

        public void setRamInMb(int value)
        {
            this.ramInMb = value;
        }

    }

    public static class NetworkConfiguration
    {
        /** OMAPI address */
        protected String dhcpAddress;

        /** OMAPI port */
        protected int dhcpPort;

        protected int rdport;

        protected String rdPassword;

        /** Check if the DVS feature is enabled; */
        protected boolean dvsEnabled;

        public boolean isDvsEnabled()
        {
            return dvsEnabled;
        }

        public void setDvsEnabled(boolean dvsEnabled)
        {
            this.dvsEnabled = dvsEnabled;
        }

        public void setRdPort(int rdport)
        {
            this.rdport = rdport;
        }

        public int getRdPort()
        {
            return rdport;
        }

        public String getRdPassword()
        {
            return rdPassword;
        }

        public void setRdPassword(final String rdPassword)
        {
            this.rdPassword = rdPassword;
        }

        public String getDhcpAddress()
        {
            return dhcpAddress;
        }

        public void setDhcpAddress(String dhcpAddress)
        {
            this.dhcpAddress = dhcpAddress;
        }

        public int getDhcpPort()
        {
            return dhcpPort;
        }

        public void setDhcpPort(int dhcpPort)
        {
            this.dhcpPort = dhcpPort;
        }

        /**
         * Null or empty password
         */
        @JsonIgnore
        public boolean isRdPasswordSet()
        {
            return rdPassword != null && !rdPassword.isEmpty();
        }

        protected List<VirtualNIC> virtualNICs;

        public List<VirtualNIC> getVirtualNIC()
        {
            if (virtualNICs == null)
            {
                virtualNICs = new ArrayList<VirtualNIC>();
            }

            return this.virtualNICs;
        }
    }

    public static class PrimaryDisk
    {
        protected DiskStandard diskStandard;

        protected DiskStateful diskStateful;

        /** If not ''requiresMoveToDatastore'' then is a HA deploy. */
        protected boolean requiresMoveToDatastore;

        public DiskStandard getDiskStandard()
        {
            return diskStandard;
        }

        public void setDiskStandard(DiskStandard diskStandard)
        {
            this.diskStandard = diskStandard;
        }

        public DiskStateful getDiskStateful()
        {
            return diskStateful;
        }

        public void setDiskStateful(DiskStateful value)
        {
            this.diskStateful = value;
        }

        /**
         * Only DiskStandard
         */
        public boolean isRequiresMoveToDatastore()
        {
            return requiresMoveToDatastore;
        }

        public void setRequiresMoveToDatastore(boolean requiresMoveToDatastore)
        {
            this.requiresMoveToDatastore = requiresMoveToDatastore;
        }

        @JsonIgnore
        public boolean isStateful()
        {
            return (getDiskStateful() != null);
        }

        @JsonIgnore
        public boolean isStandard()
        {
            return (getDiskStandard() != null);
        }

        /**
         * @return the destination datastore (even if standard or stateful disk)
         */
        @JsonIgnore
        public String getDestinationDatastore()
        {
            return isStateful() ? getDiskStateful().getDestinationDatastore() : getDiskStandard()
                .getDestinationDatastore();
        }
    }

    public static class SecondaryDisks
    {
        protected List<AuxiliaryDisk> auxiliaryDisks;

        public List<AuxiliaryDisk> getAuxiliaryDisks()
        {
            if (auxiliaryDisks == null)
            {
                auxiliaryDisks = new ArrayList<AuxiliaryDisk>();
            }

            return this.auxiliaryDisks;
        }
    }
}
