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

package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = PricingCostCode.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = PricingCostCode.TABLE_NAME)
public class PricingCostCode extends DefaultEntityBase
{
    public static final String TABLE_NAME = "pricingCostCode";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected PricingCostCode()
    {
        // Just for JPA support
    }

    public PricingCostCode(final BigDecimal price, final PricingTemplate pricingTemplate,
        final CostCode costCode)
    {
        super();
        setPrice(price);
        setCostCode(costCode);
        setPricingTemplate(pricingTemplate);
    }

    private final static String ID_COLUMN = "idPricingCostCode";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String PRICE_PROPERTY = "price";

    private final static String PRICE_COLUMN = "price";

    @Column(name = PRICE_COLUMN, nullable = true)
    private BigDecimal price;

    public BigDecimal getPrice()
    {
        return this.price;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }

    public final static String PRICING_TEMPLATE_PROPERTY = "pricingTemplate";

    private final static boolean PRICING_TEMPLATE_REQUIRED = true;

    private final static String PRICING_TEMPLATE_ID_COLUMN = "idPricingTemplate";

    @JoinColumn(name = PRICING_TEMPLATE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_pricingTemplate")
    private PricingTemplate pricingTemplate;

    @Required(value = PRICING_TEMPLATE_REQUIRED)
    public PricingTemplate getPricingTemplate()
    {
        return this.pricingTemplate;
    }

    public void setPricingTemplate(final PricingTemplate pricingTemplate)
    {
        this.pricingTemplate = pricingTemplate;
    }

    public final static String COST_CODE_PROPERTY = "costCode";

    private final static boolean COST_CODE_REQUIRED = true;

    private final static String COST_CODE_ID_COLUMN = "idCostCode";

    @JoinColumn(name = COST_CODE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_costCode")
    private CostCode costCode;

    @Required(value = COST_CODE_REQUIRED)
    public CostCode getCostCode()
    {
        return this.costCode;
    }

    public void setCostCode(final CostCode costCode)
    {
        this.costCode = costCode;
    }

}
