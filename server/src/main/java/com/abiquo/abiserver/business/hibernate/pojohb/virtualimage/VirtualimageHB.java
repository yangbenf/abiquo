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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualimage;

// Generated 16-oct-2008 16:52:14 by Hibernate Tools 3.2.1.GA

import org.apache.commons.lang.StringUtils;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageDecorator;
import com.abiquo.server.core.enumerator.DiskFormatType;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Virtualimage generated by hbm2java
 */
public class VirtualimageHB implements java.io.Serializable, IPojoHB<VirtualImage>, Cloneable
{

    private static final long serialVersionUID = 5382640224567158959L;

    private Integer idImage;

    private RepositoryHB repository;

    private IconHB icon;

    private CategoryHB category;

    private String name;

    private String description;

    private String pathName;

    private Long hdRequired;

    private Integer ramRequired;

    private Integer cpuRequired;

    private int treaty;

    private int deleted;

    private int shared;

    private VirtualimageHB master;

    private Integer idEnterprise;

    private DiskFormatType type;

    private String ovfId;

    private int stateful;

    private String volumePath;

    /** Size of the file containing the Disk. in bytes */
    private Long diskFileSize;

    private String costCode;

    public VirtualimageHB()
    {
    }

    public VirtualimageHB(final RepositoryHB repository, final CategoryHB category,
        final String name, final String pathName, final int treaty, final int deleted,
        final VirtualimageHB master, final int idEnterprise, final String ovfId,
        final Long diskFileSize)
    {
        this.repository = repository;
        this.category = category;
        this.name = name;
        this.pathName = pathName;
        this.treaty = treaty;
        this.deleted = deleted;
        this.shared = 0;
        this.master = master;
        this.idEnterprise = idEnterprise;
        this.ovfId = ovfId;
        this.stateful = 0;
        this.diskFileSize = diskFileSize;
    }

    public VirtualimageHB(final RepositoryHB repository, final IconHB icon,
        final CategoryHB category, final String name, final String description,
        final String pathName, final Long hdRequired, final Integer ramRequired,
        final Integer cpuRequired, final int treaty, final int deleted,
        final VirtualimageHB master, final int idEnterprise, final String ovfId,
        final Long diskFileSize, final String costCode)
    {
        this.repository = repository;
        this.icon = icon;
        this.category = category;
        this.name = name;
        this.description = description;
        this.pathName = pathName;
        this.hdRequired = hdRequired;
        this.ramRequired = ramRequired;
        this.cpuRequired = cpuRequired;
        this.treaty = treaty;
        this.deleted = deleted;
        this.shared = 0;
        this.master = master;
        this.idEnterprise = idEnterprise;
        this.ovfId = ovfId;
        this.stateful = 0;
        this.diskFileSize = diskFileSize;
        this.costCode = costCode;
    }

    public Integer getIdImage()
    {
        return idImage;
    }

    public void setIdImage(final Integer idImage)
    {
        this.idImage = idImage;
    }

    public RepositoryHB getRepository()
    {
        return repository;
    }

    public void setRepository(final RepositoryHB repository)
    {
        this.repository = repository;
    }

    public IconHB getIcon()
    {
        return icon;
    }

    public void setIcon(final IconHB icon)
    {
        this.icon = icon;
    }

    public CategoryHB getCategory()
    {
        return category;
    }

    public void setCategory(final CategoryHB category)
    {
        this.category = category;
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

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName(final String pathName)
    {
        this.pathName = pathName;
    }

    public Long getHdRequired()
    {
        return hdRequired;
    }

    public void setHdRequired(final Long hdRequired)
    {
        this.hdRequired = hdRequired;
    }

    public Integer getRamRequired()
    {
        return ramRequired;
    }

    public void setRamRequired(final Integer ramRequired)
    {
        this.ramRequired = ramRequired;
    }

    public Integer getCpuRequired()
    {
        return cpuRequired;
    }

    public void setCpuRequired(final Integer cpuRequired)
    {
        this.cpuRequired = cpuRequired;
    }

    public int getTreaty()
    {
        return treaty;
    }

    public void setTreaty(final int treaty)
    {
        this.treaty = treaty;
    }

    public int getDeleted()
    {
        return deleted;
    }

    public void setDeleted(final int deleted)
    {
        this.deleted = deleted;
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

    public void setDiskFileSize(Long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    public int getShared()
    {
        return shared;
    }

    public void setShared(int shared)
    {
        this.shared = shared;
    }

    public String getCostCode()
    {
        return costCode;
    }

    public void setCostCode(String costCode)
    {
        this.costCode = costCode;
    }

    @Override
    public VirtualImage toPojo()
    {
        VirtualImage virtualImage = new VirtualImage();
        return fillPojo(virtualImage);
    }

    public VirtualImage toDecorator()
    {
        VirtualImageDecorator virtualImage = new VirtualImageDecorator();
        virtualImage.setVolumePath(volumePath);

        return fillPojo(virtualImage);
    }

    private VirtualImage fillPojo(final VirtualImage virtualImage)
    {
        virtualImage.setCategory(category.toPojo());
        virtualImage.setCpuRequired(cpuRequired);
        virtualImage.setDeleted(deleted != 0);
        virtualImage.setDescription(description);
        virtualImage.setHdRequired(hdRequired);

        if (icon != null)
        {
            virtualImage.setIcon(icon.toPojo());
        }

        virtualImage.setId(idImage);
        virtualImage.setName(name);
        virtualImage.setPath(pathName);
        virtualImage.setRamRequired(ramRequired);
        virtualImage.setShared(shared);

        if (repository == null)
        {
            virtualImage.setRepository(null);
        }
        else
        {
            virtualImage.setRepository(repository.toPojo());
        }

        virtualImage
            .setDiskFormatType(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(type));
        if (master != null)
        {
            virtualImage.setMaster(master.toPojo());
        }
        virtualImage.setIdEnterprise(idEnterprise);
        virtualImage.setOvfId(ovfId);
        virtualImage.setStateful(stateful);
        virtualImage.setDiskFileSize(diskFileSize);
        virtualImage.setCostCode(costCode);

        return virtualImage;
    }

    public String getDirectoryPath()
    {
        String[] pathSplitted = getPathName().split("/");

        return StringUtils.join(Arrays.copyOfRange(pathSplitted, 0, pathSplitted.length - 1), "/");
    }

    @Override
    public VirtualimageHB clone() throws CloneNotSupportedException
    {
        return (VirtualimageHB) super.clone();
    }

    public VirtualimageHB getMaster()
    {
        return master;
    }

    public void setMaster(final VirtualimageHB master)
    {
        this.master = master;
    }

    public VirtualimageHB bundle()
    {
        VirtualimageHB imageBundled = new VirtualimageHB();

        imageBundled.setCategory(getCategory());
        imageBundled.setCpuRequired(getCpuRequired());
        imageBundled.setDescription(getDescription());
        imageBundled.setHdRequired(getHdRequired());
        imageBundled.setIcon(getIcon());
        imageBundled.setOvfId(getOvfId());
        imageBundled.setRamRequired(getRamRequired());
        imageBundled.setRepository(getRepository());
        imageBundled.setTreaty(getTreaty());
        imageBundled.setType(getType());
        imageBundled.setPathName(getPathName());
        imageBundled.setDiskFileSize(getDiskFileSize());
        imageBundled.setCostCode(getCostCode());

        if (getMaster() == null)
        {
            imageBundled.setMaster(this);
        }
        else
        {
            imageBundled.setMaster(getMaster());
        }

        imageBundled.setName(getName());

        // TODO - review enterprise and user
        imageBundled.setIdEnterprise(getIdEnterprise());

        return imageBundled;
    }

    public boolean isImageStateful()
    {
        return getStateful() != 0;
    }

    public boolean isManaged()
    {
        return getRepository() != null;
    }

    public String getVolumePath()
    {
        return volumePath;
    }

    public void setVolumePath(String volumePath)
    {
        this.volumePath = volumePath;
    }

    public DiskFormatType getType()
    {
        return type;
    }

    public void setType(DiskFormatType type)
    {
        this.type = type;
    }

}
