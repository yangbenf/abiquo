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

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.pojo.IPojo;

public class Category implements IPojo<CategoryHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    private boolean isErasable;

    private boolean isDefault;

    /* ------------- Constructor ------------- */
    public Category()
    {
        id = 0;
        name = "";
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean getIsErasable()
    {
        return isErasable;
    }

    public void setIsErasable(boolean isErasable)
    {
        this.isErasable = isErasable;
    }

    public boolean getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    public CategoryHB toPojoHB()
    {
        CategoryHB categoryHB = new CategoryHB();

        categoryHB.setIdCategory(id);
        categoryHB.setName(name);
        categoryHB.setIsErasable(isErasable ? 1 : 0);
        categoryHB.setIsDefault(isDefault ? 1 : 0);
        return categoryHB;
    }

}
