/*
 * Created on Aug 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.fulcrum.security.entity.impl;

import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.PermissionSet;

/**
 * @author Eric Pugh
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RoleImpl extends SecurityEntityImpl implements Role 
{
	private PermissionSet permissionSet = new PermissionSet();
	
    /**
     * @return
     */
    public PermissionSet getPermissions()
    {
        return permissionSet;
    }

    /**
     * @param permissionSet
     */
    public void setPermissions(PermissionSet permissionSet)
    {
        this.permissionSet = permissionSet;
    }

    /**
     * 
     */
    public RoleImpl()
    {
        super();
        // TODO Auto-generated constructor stub
    }
}
