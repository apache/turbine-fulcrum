package org.apache.fulcrum.security.model.turbine.entity.impl;

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

import java.util.HashSet;
import java.util.Set;

import org.apache.fulcrum.security.entity.impl.SecurityEntityImpl;
import org.apache.fulcrum.security.model.turbine.entity.TurbineGroup;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;

/**
 * Represents the "turbine" model where permissions are in a many to many
 * relationship to roles, roles are related to groups are related to users, all
 * in many to many relationships.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id: TurbineGroup.java 223081 2004-10-07 15:11:58Z epugh $
 */
public class TurbineGroupImpl extends SecurityEntityImpl implements TurbineGroup 
{
    private Set userGroupRoleSet = new HashSet();

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

    public void addUserGroupRole(TurbineUserGroupRole userGroupRole) {
        getUserGroupRoleSet().add(userGroupRole);
    }

    public void removeUserGroupRole(TurbineUserGroupRole userGroupRole) {
        getUserGroupRoleSet().remove(userGroupRole);
    }

}