package org.apache.fulcrum.security.model.turbine.entity;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Set;

import org.apache.fulcrum.security.entity.Group;

/**
 * Represents the "turbine" model where permissions are in a many to many
 * relationship to roles, roles are related to groups are related to users, all
 * in many to many relationships.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id$
 */
public interface TurbineGroup extends Group
{
    /**
     * Get the User/Group/Role set associated with this group
     *
     * @return a set of User/GRoup/Role relations
     */
    public Set getUserGroupRoleSet();

    /**
     * Get the User/Group/Role set associated with this group
     *
     * @param userGroupRoleSet a set of User/GRoup/Role relations
     */
    public void setUserGroupRoleSet(Set userGroupRoleSet);

    /**
     * Add a User/Group/Role relation to this group
     *
     * @param userGroupRole a User/GRoup/Role relation to add
     */
    public void addUserGroupRole(TurbineUserGroupRole userGroupRole);

    /**
     * Remove a User/Group/Role relation from this group
     *
     * @param userGroupRole a User/GRoup/Role relation to remove
     */
    public void removeUserGroupRole(TurbineUserGroupRole userGroupRole);
}