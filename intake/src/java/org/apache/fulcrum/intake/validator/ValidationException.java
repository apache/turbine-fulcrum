package org.apache.fulcrum.intake.validator;

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

import org.apache.fulcrum.intake.IntakeException;

/**
 * An Exception to mark a failed validation
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class ValidationException
        extends IntakeException
{
    /**
	 * Serial version
	 */
	private static final long serialVersionUID = -5934465742157387249L;

	/**
     * Creates a new <code>ValidationException</code> instance.
     *
     * @param message describing the reason validation failed.
     */
    public ValidationException(String message)
    {
        super(message);
    }
}
