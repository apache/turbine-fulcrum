package org.apache.fulcrum.security.hibernate;
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
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.spi.AbstractRoleManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 *
 * This implementation persists to a database via Hibernate.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateRoleManagerImpl extends AbstractRoleManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(HibernateRoleManagerImpl.class);
    
	private PersistenceHelper persistenceHelper;
   
   
   
    /**
    * Renames an existing Role.
    *
    * @param role The object describing the role to be renamed.
    * @param name the new name for the role.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the role does not exist.
    */
    public synchronized void renameRole(Role role, String name) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        roleExists = checkExists(role);
        if (roleExists)
        {
            role.setName(name);
            getPersistenceHelper().updateEntity(role);
            return;
        }
        else
        {
            throw new UnknownEntityException("Unknown role '" + role + "'");
        }
    }
    /**
      * Determines if the <code>Role</code> exists in the security system.
      *
      * @param roleName a <code>Role</code> value
      * @return true if the role name exists in the system, false otherwise
      * @throws DataBackendException when more than one Role with
      *         the same name exists.
      */
    public boolean checkExists(String roleName) throws DataBackendException
    {
        List roles;
        try
        {
            
            roles = getPersistenceHelper().retrieveSession().find("from " + Role.class.getName() + " sr where sr.name=?", roleName, Hibernate.STRING);

        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving role information", e);
        }
        if (roles.size() > 1)
        {
            throw new DataBackendException("Multiple roles with same name '" + roleName + "'");
        }
        return (roles.size() == 1);
    }
    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public RoleSet getAllRoles() throws DataBackendException
    {
        RoleSet roleSet = new RoleSet();
        try
        {
            List roles = getPersistenceHelper().retrieveSession().find("from " + Role.class.getName() + "");
            roleSet.add(roles);
        }
        catch (HibernateException e)
        {
            throw new DataBackendException("Error retriving role information", e);
        }
        return roleSet;
    }
   
   
    /**
    * Creates a new role with specified attributes.
    *
    * @param role the object describing the role to be created.
    * @return a new Role object that has id set up properly.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws EntityExistsException if the role already exists.
    */
    protected synchronized Role persistNewRole(Role role) throws DataBackendException
    {
       
		getPersistenceHelper().addEntity(role);
        return role;
    }
    /**
    * Removes a Role from the system.
    *
    * @param role The object describing the role to be removed.
    * @throws DataBackendException if there was an error accessing the data
    *         backend.
    * @throws UnknownEntityException if the role does not exist.
    */
    public synchronized void removeRole(Role role) throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = checkExists(role);
            if (roleExists)
            {
				getPersistenceHelper().removeEntity(role);
            }
            else
            {
                throw new UnknownEntityException("Unknown role '" + role + "'");
            }
        }
        catch (Exception e)
        {
            log.error("Failed to delete a Role");
            log.error(e);
            throw new DataBackendException("removeRole(Role) failed", e);
        }
    }
    
	/**
	 * @return Returns the persistenceHelper.
	 */
	public PersistenceHelper getPersistenceHelper() throws DataBackendException
	{
		if (persistenceHelper == null)
		{
			persistenceHelper = (PersistenceHelper)resolve(PersistenceHelper.ROLE);
		}
		return persistenceHelper;
	}
	
	/**
	 * Retrieve a Role object with specified id.
	 * 
	 * @param id
	 *            the id of the Role.
	 * @return an object representing the Role with specified id.
	 * @throws DataBackendException
	 *             if there was an error accessing the data backend.
	 * @throws UnknownEntityException
	 *             if the role does not exist.
	 */
	public Role getRoleById(Object id)
	throws DataBackendException, UnknownEntityException {
		
		Role role = null;

		if (id != null)
			try {
				List roles =
					getPersistenceHelper().retrieveSession().find(
							"from " + Role.class.getName() + " sr where sr.id=?",
							id,
							Hibernate.LONG);
				if (roles.size() == 0) {
					throw new UnknownEntityException(
							"Could not find role by id " + id);
				}
				role = (Role) roles.get(0);
				
			} catch (HibernateException e) {
				throw new DataBackendException(
						"Error retriving role information",
						e);
			}
			
		return role;
	}
}