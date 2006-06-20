package org.apache.fulcrum.security.model.turbine.entity;

/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy ofs the License at
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

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.RoleSet;

/**
 * Represents the "turbine" model where permissions are in a many to many
 * relationship to roles, roles are related to groups are related to users, all
 * in many to many relationships.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id$
 */
public class TurbinePermission extends SecurityEntityImpl implements Permission {
    private Set roleSet = new RoleSet();

    /**
     * @return
     */
    public RoleSet getRoles() {
        if (roleSet instanceof RoleSet)
            return (RoleSet) roleSet;
        else {
            roleSet = new RoleSet(roleSet);
            return (RoleSet) roleSet;
        }
    }

    /**
     * @return
     */
    public Set getRolesAsSet() {
        return roleSet;
    }

    public void setRolesAsSet(Set roles) {
        this.roleSet = roles;
    }

    /**
     * @param roleSet
     */
    public void setRoles(RoleSet roleSet) {
        if (roleSet != null)
            this.roleSet = roleSet;
        else
            this.roleSet = new RoleSet();
    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param permission
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param permission
     */
    public void removeRole(Role role) {
        getRoles().remove(role);
    }

   

   
}