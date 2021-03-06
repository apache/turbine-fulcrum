package org.apache.fulcrum.security.memory;

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
import java.util.ArrayList;
import java.util.List;

import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.spi.AbstractPermissionManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation keeps all objects in memory. This is mostly meant to help
 * with testing and prototyping of ideas.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: MemoryPermissionManagerImpl.java 1374008 2012-08-16 19:42:18Z
 *          tv $
 */
public class MemoryPermissionManagerImpl extends AbstractPermissionManager
{
    // static to simulate database ?
    private static List<Permission> permissions = new ArrayList<Permission>();
    
    public MemoryPermissionManagerImpl() {
        permissions.clear();
    }

    /** Our Unique ID counter */
    // private static int uniqueId = 0;

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all permissions defined in the system.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     */
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        return new PermissionSet(permissions);
    }

    /**
     * Renames an existing Permission.
     *
     * @param permission
     *            The object describing the permission to be renamed.
     * @param name
     *            the new name for the permission.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the permission does not exist.
     */
    public synchronized void renamePermission(Permission permission, String name) throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
                permission.setName(name);
                permissions.add(permission);
                return;
            }
        }
        catch (DataBackendException e)
        {
            throw new DataBackendException("renamePermission(Permission,name)", e);
        }

        throw new UnknownEntityException("Unknown permission '" + permission + "'");
    }

    /**
     * Determines if the <code>Permission</code> exists in the security system.
     *
     * @param permissionName
     *            a <code>String</code> value
     * @return <code>true</code> if the permission exists in the system, false otherwise
     * @throws DataBackendException
     *             when more than one Permission with the same name exists.
     */
    public boolean checkExists(String permissionName) throws DataBackendException
    {
        return MemoryHelper.checkExists(permissions, permissionName);
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission
     *            The object describing the permission to be removed.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the permission does not exist.
     */
    public synchronized void removePermission(Permission permission) throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permissions.remove(permission);
                return;
            }
        }
        catch (DataBackendException e)
        {
            throw new DataBackendException("removePermission(Permission)", e);
        }

        throw new UnknownEntityException("Unknown permission '" + permission + "'");
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission
     *            the object describing the permission to be created.
     * @return a new Permission object that has id set up properly.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     */
    @Override
    protected synchronized <T extends Permission> T persistNewPermission(T permission) throws DataBackendException
    {
        permission.setId(MemoryHelper.getUniqueId());
        permissions.add(permission);
        return permission;
    }
}
