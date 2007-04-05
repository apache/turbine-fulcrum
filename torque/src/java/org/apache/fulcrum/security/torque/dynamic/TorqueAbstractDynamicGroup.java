package org.apache.fulcrum.security.torque.dynamic;
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
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup;
import org.apache.fulcrum.security.torque.TorqueAbstractSecurityEntity;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupPeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRole;
import org.apache.fulcrum.security.torque.om.TorqueDynamicGroupRolePeer;
import org.apache.fulcrum.security.torque.om.TorqueDynamicRole;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUser;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroup;
import org.apache.fulcrum.security.torque.om.TorqueDynamicUserGroupPeer;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UserSet;
import org.apache.torque.TorqueException;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.util.Criteria;
/**
 * This abstract class provides the SecurityInterface to the managers.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:$
 */
public abstract class TorqueAbstractDynamicGroup extends TorqueAbstractSecurityEntity
    implements DynamicGroup
{
    /** a cache of user objects */
    private Set userSet = null;
    
    /** a cache of role objects */
    private Set roleSet = null;
    
    /**
     * Forward reference to generated code
     * 
     * Get a list of association objects, pre-populated with their TorqueDynamicUser 
     * objects.
     * 
     * @param criteria Criteria to define the selection of records
     * @throws TorqueException
     * 
     * @return a list of User/Group relations
     */
    protected abstract List getTorqueDynamicUserGroupsJoinTorqueDynamicUser(Criteria criteria) 
        throws TorqueException;

    /**
     * Forward reference to generated code
     * 
     * Get a list of association objects, pre-populated with their TorqueDynamicRole 
     * objects.
     * 
     * @param criteria Criteria to define the selection of records
     * @throws TorqueException
     * 
     * @return a list of Role/Group relations
     */
    protected abstract List getTorqueDynamicGroupRolesJoinTorqueDynamicRole(Criteria criteria) 
        throws TorqueException;

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#addUser(org.apache.fulcrum.security.entity.User)
     */
    public void addUser(User user)
    {
        getUsers().add(user);
    }

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#getUsers()
     */
    public UserSet getUsers()
    {
        if (userSet == null)
        {
            userSet = new UserSet();
        }
        else if(!(userSet instanceof UserSet))
        {
            userSet = new UserSet(userSet);
        }

        return (UserSet)userSet;
    }

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#getUsersAsSet()
     */
    public Set getUsersAsSet()
    {
        return userSet;
    }

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#removeUser(org.apache.fulcrum.security.entity.User)
     */
    public void removeUser(User user)
    {
        getUsers().remove(user);
    }

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#setUsers(org.apache.fulcrum.security.util.UserSet)
     */
    public void setUsers(UserSet userSet)
    {
        if(userSet != null)
        {
            this.userSet = userSet;
        }
        else
        {
            this.userSet = new UserSet();
        }
    }

    /**
     * @see org.apache.fulcrum.security.model.basic.entity.BasicGroup#setUsersAsSet(java.util.Set)
     */
    public void setUsersAsSet(Set users)
    {
        setUsers(new UserSet(users));
    }
    
    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#addRole(org.apache.fulcrum.security.entity.Role)
     */
    public void addRole(Role role)
    {
        getRoles().add(role);
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#getRoles()
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
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#getRolesAsSet()
     */
    public Set getRolesAsSet()
    {
        return roleSet;
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#removeRole(org.apache.fulcrum.security.entity.Role)
     */
    public void removeRole(Role role)
    {
        getRoles().remove(role);
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#setRoles(org.apache.fulcrum.security.util.RoleSet)
     */
    public void setRoles(RoleSet roleSet)
    {
        if(roleSet != null)
        {
            this.roleSet = roleSet;
        }
        else
        {
            this.roleSet = new RoleSet();
        }
    }

    /**
     * @see org.apache.fulcrum.security.model.dynamic.entity.DynamicGroup#setRolesAsSet(java.util.Set)
     */
    public void setRolesAsSet(Set roles)
    {
        setRoles(new RoleSet(roles));
    }

    /**
     * @see org.apache.fulcrum.security.torque.TorqueAbstractSecurityEntity#getDatabaseName()
     */
    public String getDatabaseName()
    {
        return TorqueDynamicGroupPeer.DATABASE_NAME;
    }

    /**
     * @see org.apache.fulcrum.security.torque.TorqueAbstractSecurityEntity#retrieveAttachedObjects(java.sql.Connection)
     */
    public void retrieveAttachedObjects(Connection con) throws TorqueException
    {
        this.userSet = new UserSet();
        
        // the generated method that allows a Connection parameter is missing
        List usergroups = getTorqueDynamicUserGroupsJoinTorqueDynamicUser(new Criteria());

        for (Iterator i = usergroups.iterator(); i.hasNext();)
        {
            TorqueDynamicUserGroup tdug = (TorqueDynamicUserGroup)i.next(); 
            userSet.add(tdug.getTorqueDynamicUser());
        }

        this.roleSet = new RoleSet();
        
        // the generated method that allows a Connection parameter is missing
        List grouproles = getTorqueDynamicGroupRolesJoinTorqueDynamicRole(new Criteria());

        for (Iterator i = grouproles.iterator(); i.hasNext();)
        {
            TorqueDynamicGroupRole tdgr = (TorqueDynamicGroupRole)i.next(); 
            roleSet.add(tdgr.getTorqueDynamicRole());
        }
    }

    /**
     * @see org.apache.fulcrum.security.torque.TorqueAbstractSecurityEntity#update(java.sql.Connection)
     */
    public void update(Connection con) throws TorqueException
    {
        if (userSet != null)
        {
            Criteria criteria = new Criteria();
            
            /* remove old entries */
            criteria.add(TorqueDynamicUserGroupPeer.GROUP_ID, getEntityId());
            TorqueDynamicUserGroupPeer.doDelete(criteria, con);

            for (Iterator i = userSet.iterator(); i.hasNext();)
            {
                TorqueDynamicUser user = (TorqueDynamicUser)i.next();

                TorqueDynamicUserGroup ug = new TorqueDynamicUserGroup();
                ug.setUserId(user.getEntityId());
                ug.setGroupId(getEntityId());
                ug.save(con);
            }
        }
        
        if (roleSet != null)
        {
            Criteria criteria = new Criteria();
            
            /* remove old entries */
            criteria.add(TorqueDynamicGroupRolePeer.GROUP_ID, getEntityId());
            TorqueDynamicGroupRolePeer.doDelete(criteria, con);

            for (Iterator i = roleSet.iterator(); i.hasNext();)
            {
                TorqueDynamicRole role = (TorqueDynamicRole)i.next();

                TorqueDynamicGroupRole gr = new TorqueDynamicGroupRole();
                gr.setRoleId(role.getEntityId());
                gr.setGroupId(getEntityId());
                gr.save(con);
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
     * @see org.apache.fulcrum.security.torque.TorqueAbstractSecurityEntity#delete()
     */
    public void delete() throws TorqueException
    {
        TorqueDynamicGroupPeer.doDelete(SimpleKey.keyFor(getEntityId()));
    }
}