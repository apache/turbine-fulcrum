package org.apache.fulcrum.security.model.turbine;

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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.turbine.entity.TurbineGroup;
import org.apache.fulcrum.security.model.turbine.entity.TurbineRole;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.spi.AbstractManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * Holds shared functionality between different implementations of
 * TurbineModelManager's.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh </a>
 * @version $Id: AbstractDynamicModelManager.java,v 1.2 2004/07/07 18:18:09
 *          epugh Exp $
 */
public abstract class AbstractTurbineModelManager extends AbstractManager implements TurbineModelManager, Configurable
{
	
    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String globalGroupName;
    
    //private boolean cascadeDelete;
	// ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    @Override
	public void configure(Configuration conf)
    {
    	globalGroupName = conf.getAttribute(
    			TurbineModelManager.GLOBAL_GROUP_ATTR_NAME,
    			TurbineModelManager.GLOBAL_GROUP_NAME);
    	//cascadeDelete = conf.getAttributeAsBoolean( TurbineModelManager.CASCADE_DELETE_ATTR_NAME, false );
    }
    
    /**
     * Provides a reference to the Group object that represents the <a
     * href="#global">global group </a>.
     * 
     * @return A Group object that represents the global group.
     */
    @Override
	public Group getGlobalGroup() throws DataBackendException
    {
        Group g = null;
        try
        {
            g = getGroupManager().getGroupByName(globalGroupName);
        }
        catch (UnknownEntityException uee)
        {
            g = getGroupManager().getGroupInstance(globalGroupName);
            try
            {
                getGroupManager().addGroup(g);
            }
            catch (EntityExistsException eee)
            {
                throw new DataBackendException(eee.getMessage(), eee);
            }

        }
        return g;
    }

    /**
     * Revokes all permissions from a Role.
     * 
     * This method is used when deleting a Role.
     * 
     * @param role
     *            the Role
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the Role is not present.
     */
    @Override
	public synchronized void revokeAll(Role role) throws DataBackendException, UnknownEntityException
    {
        revokeAll( role, false );
    }
    
    /**
     * Revokes by default all permissions from a Role and if flag is set
     * all groups and users for this role
     * 
     * This method is used when deleting a Role.
     * 
     * @param role
     *            the Role
     * @param cascadeDelete
     *             if <code>true </code> removes all groups and user for this role.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the Role is not present.
     */
    @Override
    public synchronized void revokeAll(Role role, boolean cascadeDelete) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        roleExists = getRoleManager().checkExists(role);
        if (roleExists)
        {

            Object permissions[] = ((TurbineRole) role).getPermissions().toArray();
            for (Object permission : permissions)
            {
                revoke(role, (Permission) permission);
            }
            if (cascadeDelete) {
                Object userGroupRoles[] = ((TurbineRole) role).getUserGroupRoleSet().toArray();
                for (Object userGroupRole : userGroupRoles)
                {
                    TurbineUserGroupRole ugr = (TurbineUserGroupRole) userGroupRole;
                    revoke(ugr.getUser(), ugr.getGroup(), role);
                }
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }

    }

    /**
     * Revokes all roles and groups from a User.
     * 
     * This method is used when deleting a User.
     * 
     * @param user
     *            the User
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the Role is not present.
     */
    @Override
	public synchronized void revokeAll(User user) throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        userExists = getUserManager().checkExists(user);
        if (userExists)
        {

            Object userGroupRoles[] = ((TurbineUser) user).getUserGroupRoleSet().toArray();
            for (Object userGroupRole : userGroupRoles)
            {
                TurbineUserGroupRole ugr = (TurbineUserGroupRole) userGroupRole;
                revoke(user, ugr.getGroup(), ugr.getRole());
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }
    
    /**
     * Revokes all roles and users from a Group.
     * 
     * This method is used when deleting a User.
     * 
     * @param group
     *            the Group
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the Group is not present.
     */
    @Override
    public synchronized void revokeAll(Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        groupExists = getGroupManager().checkExists(group);
        if (groupExists)
        {

            Object userGroupRoles[] = ((TurbineGroup) group).getUserGroupRoleSet().toArray();
            for (Object userGroupRole : userGroupRoles)
            {
                TurbineUserGroupRole ugr = (TurbineUserGroupRole) userGroupRole;
                revoke(ugr.getUser(), group, ugr.getRole());
            }
        }
        else
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
    }

	@Override
	public String getGlobalGroupName() {
		return globalGroupName;
	}
}
