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

package com.abiquo.tarantino.errors;



/***
 * TODO
 */
public enum VirtualFactoryErrors
{
    
    
	
	VIRTUAL_MACHINE_NOT_EXIST("ASDFASF","ASDF"),
    
	
	VIRTUAL_MACHINE_ALREADY_EXIST("ASDFASF","try to configure a virtual machine with a uuid already present on the hypervisor"),
	
	
	COMMUNITY_ONLY_STATELESS_DISKS("adfasdf","try to configure an statefull virtual image using community edition"),
	
	
	
	CONFIG("asdf","asdfasdfasdfs"),
    RECONFIG("asdf","adsf"),
    DEPLOY("adfas","asdfasd"),
    CREATE_VM("afasdf","asdfasdf"),
    
    SNAPSHOT("adfadf","asdfadsf"),
    
    CLONING_DISK("afasdf","affqsdfr"),
    
    NETWORK_DECONFIGURE("afsd","Can not deconfigure the network resources of virtual machine"),
    
    EXECUTING_ACTION("afasdf","asdfasd"),
    
    NETWORK_VSWITCH_PORT("adfa", "The port group attached to the virtual switch doesn't match the expected virtual switch"),

    NETWORK_VSWITCH_NOT_FOUND("asdfas", "The Virtual Switch couln't be found in the hypervisor. The virtual machine networking resources can't be configured"),
    
    
    
    NETWORK_CONFIGURATION("ASDASDF","can not configure the virtual nic"),
    DATASTORE_NOT_FOUND("AASDFD","ASDFASDF"),
    DATASTORE_NOT_ACCESSIBLE("ASDFA","Specified Datastore is not accessible"),
   NETWORK_NOT_FOUND("af","can not obtain the target network"),
    
    HYPERVISOR_LICENSE("hyp-00", "The hypervisor licese version is not compatible with abiquo."),


    HYPERVISOR_CONNECTION("HYP-01", "Invalid hypervisor (ip, port or protocol) location."),

    
    REPOSITORY_CONFIGURATION("la la ", "Can not configure the repository datastore"),
    
    //
    
    VIRTUAL_MACHINE_NOT_FOUND("la la", "Virtual Machine not found on the hypervisor"),
    
    VIRTUAL_MACHINE_RETRIEVE_ERROR("la la", "Can't obtain the virtual machine");
    ;

    /**
     * Internal error code
     */
    String code;

    /**
     * Description message
     */
    String message;

    public String getCode()
    {
        return String.valueOf(this.code);
    }

    public String getMessage()
    {
        return this.message;
    }

    VirtualFactoryErrors(String code, String message)
    {
        this.code = code;
        this.message = message;
    }

}
