package org.apache.fulcrum.security.model.basic;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.ACLFactory;
import org.apache.fulcrum.security.model.basic.entity.BasicUser;
import org.apache.fulcrum.security.spi.AbstractManager;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
/**
 * 
 * This factory creates instance of the DynamicAccessControlList
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class BasicACLFactory extends AbstractManager implements ACLFactory
{

    /** Logging */
    private static Log log = LogFactory.getLog(BasicACLFactory.class);
	 /**
     * Construct a new ACL object.
     *
     * This constructs a new ACL object from the configured class and
     * initializes it with the supplied roles and permissions.
     *
     * @param roles The roles that this ACL should contain
     * @param permissions The permissions for this ACL
     *
     * @return an object implementing ACL interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    private AccessControlList getAclInstance(GroupSet groupSet)
        throws UnknownEntityException
    {
        Object[] objects = { groupSet};
        String[] signatures = { GroupSet.class.getName()};
        AccessControlList accessControlList;
        try
        {
            /*
             * 
             @todo I think this is overkill for now..
            accessControlList =
                (AccessControlList) aclFactoryService.getInstance(aclClass.getName(), objects, signatures);
                */
            accessControlList =
                new BasicAccessControlListImpl(groupSet);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                "Failed to instantiate an ACL implementation object",
                e);
        }
        return accessControlList;
    }
    public AccessControlList getAccessControlList(User user)
    {
        GroupSet groupSet = ((BasicUser)user).getGroups();
        
        try
        {
            return getAclInstance(groupSet);
        }
        catch (UnknownEntityException uue)
        {
            throw new RuntimeException(uue.getMessage(), uue);
        }
    }

}