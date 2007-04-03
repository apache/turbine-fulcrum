package org.apache.fulcrum.security.model.dynamic.entity.impl;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.Set;

import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.basic.entity.impl.BasicGroupImpl;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.util.RoleSet;

/**
 * Represents the "dynamic" model where permissions are related to roles,
 * roles are related to groups and groups are related to userSet,
 * all in many to many relationships.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: DynamicGroup.java 223057 2004-07-05 19:28:23Z epugh $
 */
public class DynamicGroupImpl extends BasicGroupImpl implements DynamicGroup
{
    private Set roleSet = new RoleSet();

    /**
     * @return
     */
    public RoleSet getRoles()
    {
    	if( roleSet instanceof RoleSet )
    		return (RoleSet) roleSet;
    	else {
    		roleSet = new RoleSet(roleSet);
    		return (RoleSet)roleSet;
    	}
    }
    /**
     * @param roleSet
     */
    public void setRoles(RoleSet roleSet)
    {
    	if( roleSet != null )
    		this.roleSet = roleSet;
    	else
    		this.roleSet = new RoleSet();
    }
    public void addRole(Role role)
    {
        getRoles().add(role);
    }
    public void removeRole(Role role)
    {
        getRoles().remove(role);
    }

	public void setRolesAsSet(Set roles)
	{
		this.roleSet = roles;
	}
	public Set getRolesAsSet()
	{
		return roleSet;
	}    
}
