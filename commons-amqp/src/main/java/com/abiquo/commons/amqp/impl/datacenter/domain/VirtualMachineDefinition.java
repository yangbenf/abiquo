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

public class VirtualMachineDefinition
{
    protected String machineID;

    protected HardwareConfiguration hardwareConfiguration;

    protected NetworkConfiguration networkConfiguration;

    protected PrimaryDisk primaryDisk;

    protected SecondaryDisks secondaryDisks;

    public String getMachineID()
    {
        return machineID;
    }

    public void setMachineID(String value)
    {
        this.machineID = value;
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
        protected int rdport;

        public void setRdPort(int rdport)
        {
            this.rdport = rdport;
        }

        public int getRdPort()
        {
            return rdport;
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
        protected DiskStandardConfiguration diskStandardConf;

        protected DiskStateful diskStateful;

        public DiskStandardConfiguration getDiskStandardConf()
        {
            return diskStandardConf;
        }

        public void setDiskStandardConf(DiskStandardConfiguration value)
        {
            this.diskStandardConf = value;
        }

        public DiskStateful getDiskStateful()
        {
            return diskStateful;
        }

        public void setDiskStateful(DiskStateful value)
        {
            this.diskStateful = value;
        }

        public boolean isStateful()
        {
            if (getDiskStateful() != null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public static class DiskStandardConfiguration
        {
            protected DiskStandard diskStandard;

            /** VirtualMachine UUID is added to build the complete destination path */
            protected String destinationDatastore;

            public DiskStandard getDiskStandard()
            {
                return diskStandard;
            }

            public void setDiskStandard(DiskStandard value)
            {
                this.diskStandard = value;
            }

            public String getDestinationDatastore()
            {
                return destinationDatastore;
            }

            public void setDestinationDatastore(String value)
            {
                this.destinationDatastore = value;
            }
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
