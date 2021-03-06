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

/**
 * A constraint has a name and a value and an optional message.
 * The name/value pair will have meaning to a Validator and the
 * message will serve as an error message in the event the Validator
 * determines the constraint is violated.
 * example:
 * name="maxLength"
 * value="255"
 * message="Value cannot be longer than 255 characters."
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public interface Constraint
{
    /**
     * Get the name of the constraint.
     *
     * @return the constraint name
     */
    String getName();

    /**
     * Get the value of the constraint.
     *
     * @return the constraint value
     */
    String getValue();

    /**
     * Get the error message.
     *
     * @return the constraint error message
     */
    String getMessage();
}
