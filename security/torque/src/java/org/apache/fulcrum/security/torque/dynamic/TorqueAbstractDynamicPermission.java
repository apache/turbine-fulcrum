package org.apache.fulcrum.security.torque.dynamic;
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
import java.sql.Connection;
import java.util.List;
import java.util.Set;

import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission;
import org.apache.fulcrum.security.torque.om.TorqueDynamicPermissionPeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRolePermission;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRolePermissionPeer;
import org.apache.fulcrum.security.torque.security.TorqueAbstractSecurityEntity;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.SimpleKey;
/**
 * This abstract class provides the SecurityInterface to the managers.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public abstract class TorqueAbstractDynamicPermission extends TorqueAbstractSecurityEntity
    implements DynamicPermission
{
    /** Serial version */
	private static final long serialVersionUID = -6857144824327889029L;
	/** a cache of role objects */
    private Set<Role> roleSet = null;

    /**
     * Forward reference to generated code
     *
     * Get a list of association objects, pre-populated with their TorqueDynamicRole
     * objects.
     *
     * @param criteria Criteria to define the selection of records
     * @param con a database connection
     * @throws TorqueException  if any database error occurs
     *
     * @return a list of Role/Permission relations
     */
    protected List<TorqueDynamicRolePermission> getTorqueDynamicRolePermissionsJoinTorqueDynamicRole(Criteria criteria, Connection con)
        throws TorqueException
    {
        criteria.and(TorqueDynamicRolePermissionPeer.PERMISSION_ID, getEntityId() );
        return TorqueDynamicRolePermissionPeer.doSelectJoinTorqueDynamicRole(criteria, con);
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#addRole(org.apache.fulcrum.security.entity.Role)
     */
    public void addRole(Role role)
    {
        getRoles().add(role);
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#getRoles()
     */
    public RoleSet getRoles()
    {
        if (roleSet == null)
        {
            roleSet = new RoleSet();
        }
        else if(!(roleSet instanceof RoleSet))
        {
            roleSet = new RoleSet(roleSet);
        }

        return (RoleSet)roleSet;
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#getRolesAsSet()
     */
    @SuppressWarnings("unchecked")
	public <T extends Role> Set<T> getRolesAsSet()
    {
        return (Set<T>)roleSet;
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#removeRole(org.apache.fulcrum.security.entity.Role)
     */
    public void removeRole(Role role)
    {
        getRoles().remove(role);
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#setRoles(org.apache.fulcrum.security.util.RoleSet)
     */
    public void setRoles(RoleSet roleSet)
    {
        if (roleSet != null)
        {
            this.roleSet = roleSet;
        }
        else
        {
            this.roleSet = new RoleSet();
        }
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicPermission#setRolesAsSet(java.util.Set)
     */
    public <T extends Role> void setRolesAsSet(Set<T> roles)
    {
        setRoles(new RoleSet(roles));
    }

    /**
     * @return the database name
     */
    public String getDatabaseName()
    {
        return TorqueDynamicPermissionPeer.DATABASE_NAME;
    }
    
    @Override
    public void retrieveAttachedObjects( Connection con )
        throws DataBackendException
    {
        retrieveAttachedObjects( con, false );
    }

    /**
     * @see org.apache.fulcrum.security.torque.security.TorqueAbstractSecurityEntity#retrieveAttachedObjects(Connection, Boolean)
     */
    @Override
    public void retrieveAttachedObjects( Connection con, Boolean lazy )
        throws DataBackendException
    {
        this.roleSet = new RoleSet();

        try {
            List<TorqueDynamicRolePermission> rolepermissions = getTorqueDynamicRolePermissionsJoinTorqueDynamicRole(new Criteria(), con);
    
            for (TorqueDynamicRolePermission tdrp : rolepermissions)
            {
                roleSet.add(tdrp.getTorqueDynamicRole());
            }
        } catch (TorqueException e ) {
            throw new DataBackendException( e.getMessage(),e );
        }
    }

    /**
     * @see org.apache.fulcrum.security.torque.security.TorqueAbstractSecurityEntity#update(java.sql.Connection)
     */
    public void update(Connection con) throws TorqueException
    {
        if (roleSet != null)
        {
            Criteria criteria = new Criteria();

            /* remove old entries */
            criteria.where(TorqueDynamicRolePermissionPeer.PERMISSION_ID, getEntityId());
            TorqueDynamicRolePermissionPeer.doDelete(criteria, con);

            for (Role r : roleSet)
            {
                TorqueDynamicRolePermission rp = new TorqueDynamicRolePermission();
                rp.setRoleId((Integer)r.getId());
                rp.setPermissionId(getEntityId());
                rp.save(con);
            }
        }

        try
        {
            save(con);
        }
        catch (Exception e)
        {
            throw new TorqueException(e);
        }
    }

    /**
     * @see org.apache.fulcrum.security.torque.security.TorqueAbstractSecurityEntity#delete()
     */
    public void delete() throws TorqueException
    {
        TorqueDynamicPermissionPeer.doDelete(SimpleKey.keyFor(getEntityId()));
    }
}
