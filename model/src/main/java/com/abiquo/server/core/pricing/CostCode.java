package com.abiquo.server.core.pricing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = CostCode.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = CostCode.TABLE_NAME)
public class CostCode extends DefaultEntityBase
{
    public static final String TABLE_NAME = "costCode";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected CostCode()
    {
        // Just for JPA support
    }

    public CostCode(final String variable)
    {
        this.setVariable(variable);
    }

    /* package */final static String ID_COLUMN = "idCostCode";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String VARIABLE_PROPERTY = "variable";

    private final static boolean VARIABLE_REQUIRED = true;

    private final static int VARIABLE_LENGTH_MIN = 0;

    private final static int VARIABLE_LENGTH_MAX = 255;

    private final static boolean VARIABLE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VARIABLE_COLUMN = "variable";

    @Column(name = VARIABLE_COLUMN, nullable = !VARIABLE_REQUIRED, length = VARIABLE_LENGTH_MAX)
    private String variable;

    @Required(value = VARIABLE_REQUIRED)
    @Length(min = VARIABLE_LENGTH_MIN, max = VARIABLE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VARIABLE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVariable()
    {
        return this.variable;
    }

    private void setVariable(final String variable)
    {
        this.variable = variable;
    }

    public final static String BLOCKED_PROPERTY = "blocked";

    private final static String BLOCKED_COLUMN = "blocked";

    private final static boolean BLOCKED_REQUIRED = true;

    @Column(name = BLOCKED_COLUMN, nullable = !BLOCKED_REQUIRED)
    private boolean blocked;

    @Required(value = BLOCKED_REQUIRED)
    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(final boolean blocked)
    {
        this.blocked = blocked;
    }

}