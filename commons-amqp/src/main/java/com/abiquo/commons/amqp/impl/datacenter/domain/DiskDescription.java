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

public class DiskDescription
{
    protected DiskFormatType format;

    protected long capacityInBytes;

    public DiskFormatType getFormat()
    {
        return format;
    }

    public void setFormat(DiskFormatType format)
    {
        this.format = format;
    }

    public long getCapacityInBytes()
    {
        return capacityInBytes;
    }

    public void setCapacityInBytes(long capacityInBytes)
    {
        this.capacityInBytes = capacityInBytes;
    }

    // TODO duplicated
    public enum DiskFormatType
    {
        UNKNOWN,

        RAW,

        INCOMPATIBLE,

        VMDK_STREAM_OPTIMIZED,

        VMDK_FLAT,

        VMDK_SPARSE,

        VHD_FLAT,

        VHD_SPARSE,

        VDI_FLAT,

        VDI_SPARSE,

        QCOW2_FLAT,

        QCOW2_SPARSE;
    }
}
