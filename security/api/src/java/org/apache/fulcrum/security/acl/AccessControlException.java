package org.apache.fulcrum.security.acl;

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
import org.apache.fulcrum.security.util.FulcrumSecurityException;

/**
 * Thrown to indicate that the User attempted to perform an operation that was
 * not permitted by the security settings.
 * 
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class AccessControlException extends FulcrumSecurityException
{
    /**
     * Serial number
     */
    private static final long serialVersionUID = 1053699577313013739L;

    /**
     * Construct an AccessControlException with specified detail message.
     * 
     * @param msg
     *            The detail message.
     */
    public AccessControlException(String msg)
    {
        super(msg);
    }
}
