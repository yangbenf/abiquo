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

package com.abiquo.abiserver.pojo.virtualimage;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.pojo.IPojo;

public class VirtualImage implements IPojo<VirtualimageHB>
{
    /** Stateless virtual image code. */
    public static final int STATELESS_IMAGE = 0;

    /** Stateful virtual image code. */
    public static final int STATEFUL_IMAGE = 1;

    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    private String description;

    private String path;

    private long hdRequired;

    private int ramRequired;

    private int cpuRequired;

    private Category category;

    private Repository repository;

    private Icon icon;

    private Boolean deleted;

    private DiskFormatType diskFormatType;

    private VirtualImage master;

    private Integer idEnterprise;

    private int shared;

    private String ovfId;

    private int stateful;

    private Boolean chef;

    /** Size of the file containing the Disk. in bytes */
    private Long diskFileSize;

    private int costCode;

    /* ------------- Constructor ------------- */
    public VirtualImage()
    {
        name = "";
        description = "";
        path = "";
        category = new Category();
        repository = new Repository();
        ovfId = "";
        stateful = 0;
        deleted = false;
        shared = 0;
        costCode = 0;
    }

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(final String path)
    {
        this.path = path;
    }

    public long getHdRequired()
    {
        return hdRequired;
    }

    public void setHdRequired(final long hdRequired)
    {
        this.hdRequired = hdRequired;
    }

    public int getRamRequired()
    {
        return ramRequired;
    }

    public void setRamRequired(final int ramRequired)
    {
        this.ramRequired = ramRequired;
    }

    public int getCpuRequired()
    {
        return cpuRequired;
    }

    public void setCpuRequired(final int cpuRequired)
    {
        this.cpuRequired = cpuRequired;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(final Category category)
    {
        this.category = category;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public void setRepository(final Repository repository)
    {
        this.repository = repository;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public void setIcon(final Icon icon)
    {
        this.icon = icon;
    }

    public Boolean getDeleted()
    {
        return deleted;
    }

    public void setDeleted(final Boolean deleted)
    {
        this.deleted = deleted;
    }

    public Boolean isChef()
    {
        return chef;
    }

    public void setChef(final Boolean isChef)
    {
        this.chef = isChef;
    }

    public DiskFormatType getDiskFormatType()
    {
        return diskFormatType;
    }

    public void setDiskFormatType(final DiskFormatType diskFormatType)
    {
        this.diskFormatType = diskFormatType;
    }

    public VirtualImage getMaster()
    {
        return master;
    }

    public void setMaster(final VirtualImage master)
    {
        this.master = master;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public String getOvfId()
    {
        return ovfId;
    }

    public void setOvfId(final String ovfId)
    {
        this.ovfId = ovfId;
    }

    public int getStateful()
    {
        return stateful;
    }

    public void setStateful(final int stateful)
    {
        this.stateful = stateful;
    }

    public Long getDiskFileSize()
    {
        return diskFileSize;
    }

    public void setDiskFileSize(final Long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    public boolean isImageStateful()
    {
        return getStateful() != 0;
    }

    public boolean isManaged()
    {
        return getRepository() != null;
    }

    public int getShared()
    {
        return shared;
    }

    public void setShared(final int shared)
    {
        this.shared = shared;
    }

    public int getCostCode()
    {
        return costCode;
    }

    public void setCostCode(final int costCode)
    {
        this.costCode = costCode;
    }

    @Override
    public VirtualimageHB toPojoHB()
    {
        VirtualimageHB virtualImageHB = new VirtualimageHB();

        virtualImageHB.setIdImage(id);
        if (repository != null)
        {
            virtualImageHB.setRepository(repository.toPojoHB());
        }
        else
        {
            virtualImageHB.setRepository(null);
        }

        if (icon != null)
        {
            virtualImageHB.setIcon(icon.toPojoHB());
        }
        else
        {
            virtualImageHB.setIcon(null);
        }
        virtualImageHB.setCategory(category.toPojoHB());
        virtualImageHB.setName(getName());
        virtualImageHB.setDescription(description);
        virtualImageHB.setPathName(getPath());
        virtualImageHB.setHdRequired(hdRequired);
        virtualImageHB.setRamRequired(ramRequired);
        virtualImageHB.setCpuRequired(cpuRequired);
        virtualImageHB.setDeleted(deleted ? 1 : 0);
        virtualImageHB.setType(diskFormatType.toEnum());
        virtualImageHB.setStateful(stateful);
        virtualImageHB.setDiskFileSize(diskFileSize);
        virtualImageHB.setShared(shared);

        if (master != null)
        {
            virtualImageHB.setMaster(master.toPojoHB());
        }

        if (idEnterprise != null && idEnterprise != 0)
        {
            virtualImageHB.setIdEnterprise(idEnterprise);
        }

        virtualImageHB.setOvfId(ovfId);
        virtualImageHB.setCostCode(costCode);

        return virtualImageHB;
    }

    public void convertPathToVhd()
    {
        String path = getPath();
        if (!path.endsWith(".vhd"))
        {
            setPath(path + ".vhd");
        }
    }

}
