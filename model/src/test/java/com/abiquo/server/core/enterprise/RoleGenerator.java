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

package com.abiquo.server.core.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RoleGenerator extends DefaultEntityGenerator<Role>
{
    PrivilegeGenerator privilegeGenerator;

    EnterpriseGenerator enterpriseGenerator;

    public static enum Permissions
    {

        ENTERPRISE_ENUMERATE, ENTERPRISE_ADMINISTER_ALL, ENTERPRISE_RESOURCE_SUMMARY_ENT, PHYS_DC_ENUMERATE, PHYS_DC_RETRIEVE_RESOURCE_USAGE, PHYS_DC_MANAGE, PHYS_DC_RETRIEVE_DETAILS,

        PHYS_DC_ALLOW_MODIFY_SERVERS, PHYS_DC_ALLOW_MODIFY_NETWORK, PHYS_DC_ALLOW_MODIFY_STORAGE, PHYS_DC_ALLOW_MODIFY_ALLOCATION, VDC_ENUMERATE, VDC_MANAGE, VDC_MANAGE_VAPP,

        VDC_MANAGE_NETWORK, VDC_MANAGE_STORAGE, VAPP_CUSTOMISE_SETTINGS, VAPP_DEPLOY_UNDEPLOY, VAPP_ASSIGN_NETWORK, VAPP_ASSIGN_VOLUME, VAPP_PERFORM_ACTIONS, VAPP_CREATE_STATEFUL,

        VAPP_CREATE_INSTANCE, APPLIB_VIEW, APPLIB_ALLOW_MODIFY, APPLIB_UPLOAD_IMAGE, APPLIB_MANAGE_REPOSITORY, APPLIB_DOWNLOAD_IMAGE, APPLIB_MANAGE_CATEGORIES, USERS_VIEW,

        USERS_MANAGE_ENTERPRISE, USERS_MANAGE_USERS, USERS_MANAGE_OTHER_ENTERPRISES, USERS_PROHIBIT_VDC_RESTRICTION, USERS_VIEW_PRIVILEGES, USERS_MANAGE_ROLES, USERS_MANAGE_ROLES_OTHER_ENTERPRISES,

        USERS_MANAGE_SYSTEM_ROLES, USERS_MANAGE_LDAP_GROUP, USERS_ENUMERATE_CONNECTED, USERS_DEFINE_AS_MANAGER, SYSCONFIG_VIEW, SYSCONFIG_ALLOW_MODIFY, EVENTLOG_VIEW_ENTERPRISE, EVENTLOG_VIEW_ALL,

        APPLIB_VM_COST_CODE, USERS_MANAGE_ENTERPRISE_BRANDING, SYSCONFIG_SHOW_REPORTS, PRICING_VIEW, PRICING_MANAGE
    }

    public static final String PRICING_VIEW = "PRICING_VIEW";

    public static final String PRICING_MANAGE = "PRICING_MANAGE";

    public RoleGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

        privilegeGenerator = new PrivilegeGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Role obj1, final Role obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Role.NAME_PROPERTY, Role.BLOCKED_PROPERTY,
            Role.ENTERPRISE_PROPERTY);
    }

    @Override
    public Role createUniqueInstance()
    {
        return createInstanceSysAdmin();
    }

    public Role createInstanceSysAdmin()
    {
        List<String> privileges = getAllPrivileges();
        return createInstance(createPrivileges(privileges));
    }

    public Role createInstanceSysAdmin(final String name)
    {
        List<String> privileges = getAllPrivileges();
        return createInstance(name, createPrivileges(privileges));
    }

    public Role createInstanceEnterprisAdmin()
    {
        List<String> privileges = new ArrayList<String>();
        privileges.add(Permissions.USERS_VIEW.toString());
        privileges.add(Permissions.USERS_VIEW_PRIVILEGES.toString());
        privileges.add(Permissions.USERS_MANAGE_USERS.toString());
        privileges.add(Permissions.USERS_PROHIBIT_VDC_RESTRICTION.toString());
        privileges.add(Permissions.VDC_ENUMERATE.toString());
        return createInstance(createPrivileges(privileges));
    }

    public Role createInstance(final Enterprise enterprise)
    {
        String name = newString(nextSeed(), Role.NAME_LENGTH_MIN, Role.NAME_LENGTH_MAX);

        Role role = new Role(name, enterprise);

        return role;
    }

    public Role createInstance()
    {
        String name = newString(nextSeed(), Role.NAME_LENGTH_MIN, Role.NAME_LENGTH_MAX);

        Role role = new Role(name);

        return role;
    }

    public Role createInstance(final String name)
    {

        Role role = new Role(name);

        return role;
    }

    public Role createInstanceBlocked()
    {
        Role role = createInstance();
        role.setBlocked(true);
        return role;
    }

    public Role createInstanceBlocked(final Privilege... privileges)
    {
        Role role = createInstanceBlocked();
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstance(final Privilege... privileges)
    {
        Role role = createInstance();
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstance(final String name, final Privilege... privileges)
    {
        Role role = createInstance(name);
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstanceEnterprise()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();

        return createInstance(enterprise);
    }

    public Role createInstance(final String name, final Enterprise enterprise)
    {

        Role role = new Role(name, enterprise);

        return role;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Role entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Collection<Privilege> privileges = entity.getPrivileges();
        for (Privilege privilege : privileges)
        {
            privilegeGenerator.addAuxiliaryEntitiesToPersist(privilege, entitiesToPersist);
            entitiesToPersist.add(privilege);
        }

    }

    private Privilege[] createPrivileges(final List<String> strings)
    {
        Privilege[] created = new Privilege[strings.size()];
        for (int i = 0; i < strings.size(); i++)
        {
            created[i] = new Privilege(strings.get(i));
        }
        return created;
    }

    public static List<String> getAllPrivileges()
    {
        return getAllPrivileges("");
    }

    public static List<String> getAllPrivileges(final String prefix)
    {
        List<String> privileges = new ArrayList<String>();

        privileges.add(prefix + Permissions.ENTERPRISE_ENUMERATE);
        privileges.add(prefix + Permissions.ENTERPRISE_ADMINISTER_ALL);
        privileges.add(prefix + Permissions.ENTERPRISE_RESOURCE_SUMMARY_ENT);
        privileges.add(prefix + Permissions.PHYS_DC_ENUMERATE);
        privileges.add(prefix + Permissions.PHYS_DC_RETRIEVE_RESOURCE_USAGE);
        privileges.add(prefix + Permissions.PHYS_DC_MANAGE);
        privileges.add(prefix + Permissions.PHYS_DC_RETRIEVE_DETAILS);
        privileges.add(prefix + Permissions.PHYS_DC_ALLOW_MODIFY_SERVERS);
        privileges.add(prefix + Permissions.PHYS_DC_ALLOW_MODIFY_NETWORK);
        privileges.add(prefix + Permissions.PHYS_DC_ALLOW_MODIFY_STORAGE);
        privileges.add(prefix + Permissions.PHYS_DC_ALLOW_MODIFY_ALLOCATION);
        privileges.add(prefix + Permissions.VDC_ENUMERATE);
        privileges.add(prefix + Permissions.VDC_MANAGE);
        privileges.add(prefix + Permissions.VDC_MANAGE_VAPP);
        privileges.add(prefix + Permissions.VDC_MANAGE_NETWORK);
        privileges.add(prefix + Permissions.VDC_MANAGE_STORAGE);
        privileges.add(prefix + Permissions.VAPP_CUSTOMISE_SETTINGS);
        privileges.add(prefix + Permissions.VAPP_DEPLOY_UNDEPLOY);
        privileges.add(prefix + Permissions.VAPP_ASSIGN_NETWORK);
        privileges.add(prefix + Permissions.VAPP_ASSIGN_VOLUME);
        privileges.add(prefix + Permissions.VAPP_PERFORM_ACTIONS);
        privileges.add(prefix + Permissions.VAPP_CREATE_STATEFUL);
        privileges.add(prefix + Permissions.VAPP_CREATE_INSTANCE);
        privileges.add(prefix + Permissions.APPLIB_VIEW);
        privileges.add(prefix + Permissions.APPLIB_ALLOW_MODIFY);
        privileges.add(prefix + Permissions.APPLIB_UPLOAD_IMAGE);
        privileges.add(prefix + Permissions.APPLIB_MANAGE_REPOSITORY);
        privileges.add(prefix + Permissions.APPLIB_DOWNLOAD_IMAGE);
        privileges.add(prefix + Permissions.APPLIB_MANAGE_CATEGORIES);
        privileges.add(prefix + Permissions.USERS_VIEW);
        privileges.add(prefix + Permissions.USERS_MANAGE_ENTERPRISE);
        privileges.add(prefix + Permissions.USERS_MANAGE_USERS);
        privileges.add(prefix + Permissions.USERS_MANAGE_OTHER_ENTERPRISES);
        privileges.add(prefix + Permissions.USERS_PROHIBIT_VDC_RESTRICTION);
        privileges.add(prefix + Permissions.USERS_VIEW_PRIVILEGES);
        privileges.add(prefix + Permissions.USERS_MANAGE_ROLES);
        privileges.add(prefix + Permissions.USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
        privileges.add(prefix + Permissions.USERS_MANAGE_SYSTEM_ROLES);
        privileges.add(prefix + Permissions.USERS_MANAGE_LDAP_GROUP);
        privileges.add(prefix + Permissions.USERS_ENUMERATE_CONNECTED);
        privileges.add(prefix + Permissions.USERS_DEFINE_AS_MANAGER);
        privileges.add(prefix + Permissions.SYSCONFIG_VIEW);
        privileges.add(prefix + Permissions.SYSCONFIG_ALLOW_MODIFY);
        privileges.add(prefix + Permissions.EVENTLOG_VIEW_ENTERPRISE);
        privileges.add(prefix + Permissions.EVENTLOG_VIEW_ALL);
        privileges.add(prefix + Permissions.APPLIB_VM_COST_CODE);
        privileges.add(prefix + Permissions.USERS_MANAGE_ENTERPRISE_BRANDING);
        privileges.add(prefix + Permissions.SYSCONFIG_SHOW_REPORTS);
        privileges.add(prefix + Permissions.PRICING_VIEW);
        privileges.add(prefix + Permissions.PRICING_MANAGE);

        return privileges;
    }
}
