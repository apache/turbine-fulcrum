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

import java.util.HashSet;
import java.util.Set;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.util.PermissionSet;

/**
 * Represents the "turbine" model where permissions are in a many to many
 * relationship to roles, roles are related to groups are related to users, all
 * in many to many relationships.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id$
 */
public class TurbineRole extends SecurityEntityImpl implements Role {
    private Set permissionSet = new PermissionSet();

    private Set userGroupRoleSet = new HashSet();

    /**
     * @return
     */
    public PermissionSet getPermissions() {
        if (permissionSet instanceof PermissionSet)
            return (PermissionSet) permissionSet;
        else {
            permissionSet = new PermissionSet(permissionSet);
            return (PermissionSet) permissionSet;
        }
    }

    /**
     * @return
     */
    public Set getPermissionsAsSet() {
        return permissionSet;
    }

    public void setPermissionsAsSet(Set permissions) {
        this.permissionSet = permissions;
        ;
    }

    /**
     * @param permissionSet
     */
    public void setPermissions(PermissionSet permissionSet) {
        if (permissionSet != null)
            this.permissionSet = permissionSet;
        else
            this.permissionSet = new PermissionSet();
    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param permission
     */
    public void addPermission(Permission permission) {
        getPermissions().add(permission);
    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param permission
     */
    public void removePermission(Permission permission) {
        getPermissions().remove(permission);
    }

    /**
     * @return
     */
    public Set getUserGroupRoleSet() {

        return userGroupRoleSet;
    }

    /**
     * @param userGroupRoleSet
     */
    public void setUserGroupRoleSet(Set userGroupRoleSet) {

        this.userGroupRoleSet = userGroupRoleSet;

    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param group
     */
    public void addUserGroupRole(TurbineUserGroupRole userGroupRole) {
        getUserGroupRoleSet().add(userGroupRole);
    }

    /**
     * This method should only be used by a RoleManager. Not directly.
     * 
     * @param group
     */
    public void removeUserGroupRole(TurbineUserGroupRole userGroupRole) {
        getUserGroupRoleSet().remove(userGroupRole);
    }

   
}