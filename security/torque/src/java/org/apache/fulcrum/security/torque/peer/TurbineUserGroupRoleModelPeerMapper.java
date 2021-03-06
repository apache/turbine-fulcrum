package org.apache.fulcrum.security.torque.peer;
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

import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.entity.User;
import org.apache.torque.TorqueException;

/**
 * This interface allows to retrieve user, group, role relationships either from custom or the default OM in 
 * 
 * It should be implemented by appopriate om classes (interface may be set in schema).
 * 
 * @author gk
 * @Id $Id$
 *
 */
public interface TurbineUserGroupRoleModelPeerMapper
{

    /**
     * Returns the associated TurbineUser object.
     * If it was not retrieved before, the object is retrieved from
     * the database
     *
     * @return the associated TurbineUser object
     * @throws TorqueException  if any database error occurs when reading from the database fails.
     */
    User getTurbineUser()
        throws TorqueException;

    /**
     * Return the associated TurbineUser object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated TurbineUser object
     * @throws TorqueException  if any database error occurs
     */
    User getTurbineUser( Connection connection )
        throws TorqueException;

    /**
     * Returns the associated TurbineGroup object.
     * If it was not retrieved before, the object is retrieved from
     * the database
     *
     * @return the associated TurbineGroup object
     * @throws TorqueException  if any database error occurs when reading from the database fails.
     */
    Group getTurbineGroup()
        throws TorqueException;

    /**
     * Return the associated TurbineGroup object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated TurbineGroup object
     * @throws TorqueException  if any database error occurs
     */
    Group getTurbineGroup( Connection connection )
        throws TorqueException;

    /**
     * Returns the associated TurbineRole object.
     * If it was not retrieved before, the object is retrieved from
     * the database
     *
     * @return the associated TurbineRole object
     * @throws TorqueException  if any database error occurs when reading from the database fails.
     */
    Role getTurbineRole()
        throws TorqueException;

    /**
     * Return the associated TurbineRole object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated TurbineRole object
     * @throws TorqueException  if any database error occurs
     */
    Role getTurbineRole( Connection connection )
        throws TorqueException;

}