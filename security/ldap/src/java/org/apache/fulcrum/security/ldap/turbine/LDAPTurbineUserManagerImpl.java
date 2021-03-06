package org.apache.fulcrum.security.ldap.turbine;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.ldap.LDAPUserManagerImpl;
import org.apache.fulcrum.security.model.turbine.TurbineUserManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;

/**
 * This implementation uses ldap for retrieving user data.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id:LDAPTurbineUserManagerImpl.java 535465 2007-05-05 06:58:06Z tv $
 */
public class LDAPTurbineUserManagerImpl extends LDAPUserManagerImpl implements TurbineUserManager
{
    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException
     *             if the implementation of User interface could not be
     *             determined, or does not exist.
     */
    public <T extends User> T getAnonymousUser() throws UnknownEntityException
    {
        T user;
        try
        {
            user = getUserInstance();
        }
        catch (DataBackendException dbe)
        {
            throw new UnknownEntityException("Coudl not create an anonymous user.", dbe);
        }
        user.setName("");
        return user;
    }

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param user
     *            An user object
     *
     * @return True if this is an anonymous user
     *
     */
    public boolean isAnonymousUser(User user)
    {
        // Either just null, the name is null or the name is the empty string
        return (user == null) || StringUtils.isEmpty(user.getName());
    }
}
