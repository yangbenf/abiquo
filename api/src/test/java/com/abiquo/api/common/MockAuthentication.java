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

package com.abiquo.api.common;

import java.util.List;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import com.abiquo.server.core.enterprise.RoleGenerator;

public class MockAuthentication implements Authentication
{

    private String name;

    public MockAuthentication(final String username)
    {
        this.name = username;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isAuthenticated()
    {
        return true;
    }

    @Override
    public GrantedAuthority[] getAuthorities()
    {
        List<String> authorityStrings = RoleGenerator.getAllPrivileges("ROLE_");
        authorityStrings.add("ROLE_AUTHENTICATED");

        GrantedAuthority[] authorities = new GrantedAuthority[authorityStrings.size()];

        int i = 0;
        for (String authString : authorityStrings)
        {
            authorities[i] = new GrantedAuthorityImpl(authString);
            i++;
        }

        return authorities;
    }

    @Override
    public Object getCredentials()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDetails()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAuthenticated(final boolean arg0) throws IllegalArgumentException
    {

    }
}
