package org.apache.fulcrum.security;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.List;

import org.apache.fulcrum.TurbineServices;

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;

import org.apache.fulcrum.security.util.AccessControlList;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.TurbineSecurityException;

import org.apache.torque.util.Criteria;

/**
 * This is a Facade class for SecurityService.
 *
 * This class provides static methods that call related methods of the
 * implementation of SecurityService used by the System, according to
 * the settings in TurbineResources.
 * <br>
 *
 * <a name="global">
 * <p> Certain Roles that the Users may have in the system may are not related
 * to any specific resource nor entity. They are assigned within a special group
 * named 'global' that can be referenced in the code as
 * {@link org.apache.fulcrum.security.entity.Group#GLOBAL_GROUP_NAME}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class TurbineSecurity
{
    /**
     * Retrieves an implementation of SecurityService, base on the settings in
     * TurbineResources.
     *
     * @return an implementation of SecurityService.
     */
    public static SecurityService getService()
    {
        return (SecurityService) TurbineServices.getInstance().
            getService(SecurityService.SERVICE_NAME);
    }

    /*-----------------------------------------------------------------------
      Getting Object Classes
      -----------------------------------------------------------------------*/

    /**
     * Returns the Class object for the implementation of User interface
     * used by the system.
     *
     * @return the implementation of User interface used by the system.
     * @throws UnknownEntityException if the system's implementation of User
     *         interface could not be determined.
     */
    public static Class getUserClass()
        throws UnknownEntityException
    {
        return getService().getUserClass();
    }

    /**
     * Returns the Class object for the implementation of Group interface
     * used by the system.
     *
     * @return the implementation of Group interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Group
     *         interface could not be determined.
     */
    public static Class getGroupClass()
        throws UnknownEntityException
    {
        return getService().getGroupClass();
    }

    /**
     * Returns the Class object for the implementation of Permission interface
     * used by the system.
     *
     * @return the implementation of Permission interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Permission
     *         interface could not be determined.
     */
    public static Class getPermissionClass()
        throws UnknownEntityException
    {
        return getService().getPermissionClass();
    }

    /**
     * Returns the Class object for the implementation of Role interface
     * used by the system.
     *
     * @return the implementation of Role interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Role
     *         interface could not be determined.
     */
    public static Class getRoleClass()
        throws UnknownEntityException
    {
        return getService().getRoleClass();
    }

    /*-----------------------------------------------------------------------
      Management of User objects
      -----------------------------------------------------------------------*/

    /**
     * This method provides client-side encryption of passwords.
     *
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties.
     *
     * @param password the password to process
     * @return processed password
     */
    public static String encryptPassword(String password)
    {
        return getService().encryptPassword(password);
    }

    /**
     * This method provides client-side encryption of passwords. 
     *   
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties. 
     *
     * @param password the password to process
     * @param salt the supplied salt to encrypt the password
     * @return processed password
     */
    public static String encryptPassword(String password, String salt)
    {
        return getService().encryptPassword(password, salt); 
    }
    
    /**
     * Checks if a supplied password matches the encrypted password
     *
     * @param checkpw      The clear text password supplied by the user
     * @param encpw        The current, encrypted password
     *
     * @return true if the password matches, else false
     *
     */
    
    public static boolean checkPassword(String checkpw, String encpw)
    {
        return getService().checkPassword(checkpw, encpw); 
    }
    
    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public static User getUserInstance()
        throws UnknownEntityException
    {
        return getService().getUserInstance();
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static boolean accountExists(User user)
        throws DataBackendException
    {
        return getService().accountExists(user);
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     *
     * @return true if the specified account exists
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static boolean accountExists(String userName)
        throws DataBackendException
    {
        return getService().accountExists(userName);
    }

    /**
     * Authenticates an user, and constructs an User object to represent him/her.
     *
     * @param username The user name.
     * @param password The user password.
     * @return An authenticated Turbine User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     * @throws PasswordMismatchException if the supplied password was incorrect.
     */
    public static User getAuthenticatedUser(String username, String password)
        throws DataBackendException, UnknownEntityException, PasswordMismatchException
    {
        return getService().getAuthenticatedUser(username, password);
    }

    /**
     * Constructs an User object to represent a registered user of the application.
     *
     * @param username The user name.
     * @return A Turbine User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public static User getUser(String username)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getUser(username);
    }

    /**
     * Retrieve a set of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public static User[] getUsers(Criteria criteria)
        throws DataBackendException
    {
        return getService().getUsers(criteria);
    }

    /**
     * Retrieve a set of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public static List getUserList(Criteria criteria)
        throws DataBackendException
    {
        return getService().getUserList(criteria);
    }

    /**
     * Constructs an User object to represent an anonymous user of the application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the anonymous User object couldn't be
     *         constructed.
     */
    public static User getAnonymousUser()
        throws UnknownEntityException
    {
        return getService().getAnonymousUser();
    }

    /**
     * Saves User's data in the permanent storage. The user account is required
     * to exist in the storage.
     *
     * @param user The User object to save.
     *
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static void saveUser(User user)
        throws UnknownEntityException, DataBackendException
    {
        getService().saveUser(user);
    }

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static void changePassword(User user, String oldPassword, String newPassword)
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        getService().changePassword(user, oldPassword, newPassword);
    }

    /**
     * Forcibly sets new password for an User.
     *
     * This is supposed by the administrator to change the forgotten or
     * compromised passwords. Certain implementatations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static void forcePassword(User user, String password)
        throws UnknownEntityException, DataBackendException
    {
        getService().forcePassword(user, password);
    }

    /*-----------------------------------------------------------------------
      Creation of AccessControlLists
      -----------------------------------------------------------------------*/

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     *
     * @return The AccessControList object constructed from the user object.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public static AccessControlList getACL(User user)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getACL(user);
    }

    /*-----------------------------------------------------------------------
      Security management
      -----------------------------------------------------------------------*/

    /**
     * Grant an User a Role in a Group.
     *
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    public static void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        getService().grant(user, group, role);
    }

    /**
     * Revoke a Role in a Group from an User.
     *
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    public static void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        getService().revoke(user, group, role);
    }

    /**
     * Revokes all roles from an User.
     *
     * This method is used when deleting an account.
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public static void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        getService().revokeAll(user);
    }

    /**
     * Grants a Role a Permission
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public static void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        getService().grant(role, permission);
    }

    /**
     * Revokes a Permission from a Role.
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public static void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        getService().revoke(role, permission);
    }

    /**
     * Revokes all permissions from a Role.
     *
     * This method is user when deleting a Role.
     *
     * @param role the Role
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws  UnknownEntityException if the Role is not present.
     */
    public static void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        getService().revokeAll(role);
    }

    /*-----------------------------------------------------------------------
      Account management
      -----------------------------------------------------------------------*/

    /**
     * Creates new user account with specified attributes.
     *
     * <strong>TODO</strong> throw more specific exception<br>
     *
     * @param user the object describing account to be created.
     * @param password The password to use.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public static void addUser(User user, String password)
        throws DataBackendException, EntityExistsException
    {
        getService().addUser(user, password);
    }


    /**
     * Removes an user account from the system.
     *
     * <strong>TODO</strong> throw more specific exception<br>
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public static void removeUser(User user)
        throws DataBackendException, UnknownEntityException
    {
        getService().removeUser(user);
    }


    /*-----------------------------------------------------------------------
      Group/Role/Permission management
      -----------------------------------------------------------------------*/
    /**
     * Provides a reference to the Group object that represents the
     * <a name="global">global group</a>.
     *
     * @return a Group object that represents the global group.
     */
    public static Group getGlobalGroup()
    {
        return getService().getGlobalGroup();
    }

    /**
     * Creates a new Group in the system. This is a convenience
     * method.
     *
     * @param name The name of the new Group.
     * @return An object representing the new Group.
     * @throws TurbineSecurityException if the Group could not be created.
     */
    public static Group createGroup(String name)
        throws TurbineSecurityException
    {
        return getService().addGroup(getNewGroup(name));
    }

    /**
     * Creates a new Permission in the system. This is a convenience
     * method.
     *
     * @param name The name of the new Permission.
     * @return An object representing the new Permission.
     * @throws TurbineSecurityException if the Permission could not be created.
     */
    public static Permission createPermission(String name)
        throws TurbineSecurityException
    {
        return getService().addPermission(getNewPermission(name));
    }

    /**
     * Creates a new Role in the system. This is a convenience
     * method.
     *
     * @param name The name of the Role.
     *
     * @return An object representing the new Role.
     *
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public static Role createRole(String name)
        throws TurbineSecurityException
    {
        return getService().addRole(getNewRole(name));
    }

    /**
     * Retrieves a named Group.
     *
     * @param groupName The name of the Group to be retrieved.
     *
     * @return an object representing the Group with specified name.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the Group is not present.
     */
    public static Group getGroup(String groupName)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getGroup(groupName);
    }

    /**
     * Retrieves a named Group. If the Group does not exist, it creates
     * a new Group based on the Services Group implementation. It is ok
     * to pass in null or "" here and then use Group.setName() at a later
     * point.
     *
     * @param groupName The name of the Group to be retrieved.
     *
     * @return an object representing the Group with specified name.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static Group getNewGroup(String groupName)
        throws DataBackendException
    {
        return getService().getNewGroup(groupName);
    }

    /**
     * Retrieves a named Permission. If the Permission does not exist, it creates
     * a new Permission based on the Services Permission implementation. It is ok
     * to pass in null or "" here and then use Permission.setName() at a later
     * point.
     *
     * @param roleName The name of the Role to be retrieved.
     *
     * @return an object representing the Role with specified name.
     *
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public static Role getNewRole(String roleName)
        throws TurbineSecurityException
    {
        return getService().getNewRole(roleName);
    }

    /**
     * Retrieves a named Permission. If the Permission does not exist, it creates
     * a new Permission based on the Services Permission implementation. It is ok
     * to pass in null or "" here and then use Permission.setName() at a later
     * point.
     *
     * @param permissionName The name of the Permission to be retrieved.
     *
     * @return an object representing the Permission with specified name.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static Permission getNewPermission(String permissionName)
        throws DataBackendException
    {
        return getService().getNewPermission(permissionName);
    }

    /**
     * Retrieves a named Role.
     *
     * @param roleName The name of the Role to be retrieved.
     *
     * @return an object representing the Role with specified name.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the Role is not present.
     */
    public static Role getRole(String roleName)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getRole(roleName);
    }

    /**
     * Retrieves a named Permission.
     *
     * @param permissionName The name of the Permission to be retrieved.
     *
     * @return an object representing the Permission with specified name.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the Permission is not present.
     */
    public static Permission getPermission(String permissionName)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getPermission(permissionName);
    }

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param criteria A Criteria of Group selection.
     *
     * @return a set of Groups that meet the specified Criteria.
     *
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static GroupSet getGroups(Criteria criteria)
        throws DataBackendException
    {
        return getService().getGroups(criteria);
    }


    /**
     * Retrieve a set of Roles that meet the specified Criteria.
     *
     * @param criteria a Criteria of Roles selection.
     * @return a set of Roles that meet the specified Criteria.
     *
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static RoleSet getRoles(Criteria criteria)
        throws DataBackendException
    {
        return getService().getRoles(criteria);
    }

    /**
     * Retrieve a set of Permissions that meet the specified Criteria.
     *
     * @param criteria a Criteria of Permissions selection.
     *
     * @return a set of Permissions that meet the specified Criteria.
     *
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static PermissionSet getPermissions(Criteria criteria)
        throws DataBackendException
    {
        return getService().getPermissions(criteria);
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     *
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static GroupSet getAllGroups()
        throws DataBackendException
    {
        return getService().getAllGroups();
    }

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static RoleSet getAllRoles()
        throws DataBackendException
    {
        return getService().getAllRoles();
    }

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public static PermissionSet getAllPermissions()
        throws DataBackendException
    {
        return getService().getAllPermissions();
    }

    /**
     * Retrieves all permissions associated with a role.
     *
     * @param role the role name, for which the permissions are to be retrieved.
     *
     * @return an PermissionSet with all permissions for the supplied role.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role is not present.
     */
    public static PermissionSet getPermissions(Role role)
        throws DataBackendException, UnknownEntityException
    {
        return getService().getPermissions(role);
    }

    /**
     * Stores Group's attributes. The Groups is required to exist in the system.
     *
     * @param group The Group to be stored.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public static void saveGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        getService().saveGroup(group);
    }

    /**
     * Stores Role's attributes. The Roles is required to exist in the system.
     *
     * @param role The Role to be stored.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public static void saveRole(Role role)
        throws DataBackendException, UnknownEntityException
    {
        getService().saveRole(role);
    }

    /**
     * Stores Permission's attributes. The Permissions is required to exist in
     * the system.
     *
     * @param permission The Permission to be stored.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public static void savePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        getService().savePermission(permission);
    }

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the group already exists.
     */
    public static void addGroup(Group group)
        throws DataBackendException, EntityExistsException
    {
        getService().addGroup(group);
    }

    /**
     * Creates a new role with specified attributes.
     *
     * @param role the objects describing the role to be created.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the role already exists.
     */
    public static void addRole(Role role)
        throws DataBackendException, EntityExistsException
    {
        getService().addRole(role);
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission the objects describing the permission to be created.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the permission already exists.
     */
    public static void addPermission(Permission permission)
        throws DataBackendException, EntityExistsException
    {
        getService().addPermission(permission);
    }

    /**
     * Removes a Group from the system.
     *
     * @param group the object describing group to be removed.
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public static void removeGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
        getService().removeGroup(group);
    }

    /**
     * Removes a Role from the system.
     *
     * @param role The object describing role to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public static void removeRole(Role role)
        throws DataBackendException, UnknownEntityException
    {
        getService().removeRole(role);
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing permission to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public static void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        getService().removePermission(permission);
    }

    /**
     * Renames an existing Group.
     *
     * @param group The object describing the group to be renamed.
     * @param name the new name for the group.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public static void renameGroup(Group group, String name)
        throws DataBackendException, UnknownEntityException
    {
        getService().renameGroup(group, name);
    }

    /**
     * Renames an existing Role.
     *
     * @param role The object describing the role to be renamed.
     * @param name the new name for the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public static void renameRole(Role role, String name)
        throws DataBackendException, UnknownEntityException
    {
        getService().renameRole(role, name);
    }

    /**
     * Renames an existing Permission.
     *
     * @param permission The object describing the permission to be renamed.
     * @param name the new name for the permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public static void renamePermission(Permission permission, String name)
        throws DataBackendException, UnknownEntityException
    {
        getService().renamePermission(permission, name);
    }
}
