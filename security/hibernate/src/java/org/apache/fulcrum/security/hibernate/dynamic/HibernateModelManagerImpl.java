package org.apache.fulcrum.security.hibernate.dynamic;
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
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.hibernate.AbstractHibernateModelManager;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicRole;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicUser;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * This implementation persists to a database via Hibernate.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateModelManagerImpl extends AbstractHibernateModelManager implements DynamicModelManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateModelManagerImpl.class);

    /**
	 * Revokes a Role from a Group.
	 * 
	 * @param group the Group.
	 * @param role the Role.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if group or role is not present.
	 */
    public synchronized void revoke(Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            roleExists = getRoleManager().checkExists(role);
            if (groupExists && roleExists)
            {
                ((DynamicGroup) group).removeRole(role);
                ((DynamicRole) role).removeGroup(group);
                getPersistenceHelper().updateEntity(group);
                //updateEntity(role);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Group,Role) failed", e);
        }
        finally
        {
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
    }
    /**
	 * Grants a Role a Permission
	 * 
	 * @param role the Role.
	 * @param permission the Permission.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if role or permission is not present.
	 */
    public synchronized void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            roleExists = getRoleManager().checkExists(role);
            permissionExists = getPermissionManager().checkExists(permission);
            
            if (roleExists && permissionExists)
            {
                ((DynamicRole) role).addPermission(permission);
                ((DynamicPermission) permission).addRole(role);
                getPersistenceHelper().updateEntity(permission);
                getPersistenceHelper().updateEntity(role);                
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '" + permission.getName() + "'");
        }
    }
    /**
	 * Revokes a Permission from a Role.
	 * 
	 * @param role the Role.
	 * @param permission the Permission.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if role or permission is not present.
	 */
    public synchronized void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            roleExists = getRoleManager().checkExists(role);
            permissionExists = getPermissionManager().checkExists(permission);
            if (roleExists && permissionExists)
            {
                ((DynamicRole) role).removePermission(permission);
                ((DynamicPermission) permission).removeRole(role);
                getPersistenceHelper().updateEntity(role);
                getPersistenceHelper().updateEntity(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Role,Permission) failed", e);
        }
        finally
        {
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '" + permission.getName() + "'");
        }
    }
    /**
	 * Revokes all permissions and groups from a Role.
	 * 
	 * This method is used when deleting a Role.
	 * 
	 * @param role the Role
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the Role is not present.
	 */
    public synchronized void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = getRoleManager().checkExists(role);
            if (roleExists)
            {
                ((DynamicRole) role).setPermissions(new PermissionSet());
				getPersistenceHelper().updateEntity(role);
				
				Object groups[] = ((DynamicRole) role).getGroups().toArray();
				
				for (int i = 0; i < groups.length; i++)
				{
					Group group = (Group) groups[i];
					revoke(group, role);
				}	
				return;            
			}
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(Role) failed", e);
        }
        finally
        {
        }
        throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
    }

    /**
	 * Puts a user in a group.
	 * 
	 * This method is used when adding a user to a group
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the account is not present.
	 */
    public synchronized void grant(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);
            if (groupExists && userExists)
            {
                ((DynamicUser) user).addGroup(group);
                ((DynamicGroup) group).addUser(user);
                getPersistenceHelper().updateEntity(group);
                getPersistenceHelper().updateEntity(user);   
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }
    /**
	 * Removes a user in a group.
	 * 
	 * This method is used when removing a user to a group
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the user or group is not present.
	 */
    public synchronized void revoke(User user, Group group) throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean userExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            userExists = getUserManager().checkExists(user);
            if (groupExists && userExists)
            {

                Session session = getPersistenceHelper().retrieveSession();
                Transaction transaction = session.beginTransaction();
				((DynamicUser) user).removeGroup(group);
				((DynamicGroup) group).removeUser(user);                
                session.update(user);
                session.update(group);
                transaction.commit();
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }

    /**
	 * Revokes all groups from a user
	 * 
	 * This method is used when deleting an account.
	 * 
	 * @param user the User.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if the account is not present.
	 */
    public synchronized void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        userExists = getUserManager().checkExists(user);
        if (userExists)
        {
            Object groups[] = ((DynamicUser) user).getGroups().toArray();

            for (int i = 0; i < groups.length; i++)
            {
                Group group = (Group) groups[i];
                revoke(user, group);
            }

            return;
        }
        else
        {
            throw new UnknownEntityException("Unknown user '" + user.getName() + "'");
        }
    }
    /**
	 * Grants a Group a Role
	 * 
	 * @param group the Group.
	 * @param role the Role.
	 * @throws DataBackendException if there was an error accessing the data backend.
	 * @throws UnknownEntityException if group or role is not present.
	 */
    public synchronized void grant(Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            groupExists = getGroupManager().checkExists(group);
            roleExists = getRoleManager().checkExists(role);
            if (groupExists && roleExists)
            {
                ((DynamicGroup) group).addRole(role);
                ((DynamicRole) role).addGroup(group);
                getPersistenceHelper().updateEntity(group);
                getPersistenceHelper().updateEntity(role);   
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Group,Role) failed", e);
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '" + role.getName() + "'");
        }
    }
    
    /**
     * Revokes all users and roles from a group
     * 
     * This method is used when deleting a group.
     * 
     * @param group the Group.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public synchronized void revokeAll(Group group)
	throws DataBackendException, UnknownEntityException
	{
    	boolean groupExists = false;
    	groupExists = getGroupManager().checkExists(group);
    	if (groupExists)
    	{
    		Object users[] = ((DynamicGroup) group).getUsers().toArray();
   		
    		for (int i = 0; i < users.length; i++)
    		{
    			User user = (User) users[i];
    			revoke(user, group);
    		}

    		Object roles[] = ((DynamicGroup) group).getRoles().toArray();
    		
    		for (int i = 0; i < roles.length; i++)
    		{
    			Role role = (Role) roles[i];
    			revoke(group, role);
    		}

    		return;
    	}
    	else
    	{
    		throw new UnknownEntityException("Unknown group '" + group.getName() + "'");
    	}
    }
    
    /**
     * Revokes all roles from a permission
     * 
     * This method is used when deleting a permission.
     * 
     * @param permission the permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public synchronized void revokeAll(Permission permission)
	throws DataBackendException, UnknownEntityException
	{
    	boolean permissionExists = false;
    	permissionExists = getPermissionManager().checkExists(permission);
    	if (permissionExists)
    	{
    		Object roles[] = ((DynamicPermission) permission).getRoles().toArray();
   		
    		for (int i = 0; i < roles.length; i++)
    		{
    		    Role role = (Role) roles[i];
    			revoke(role, permission);
    		}

    		
    		return;
    	}
    	else
    	{
    		throw new UnknownEntityException("Unknown permission '" + permission.getName() + "'");
    	}
    }    
}