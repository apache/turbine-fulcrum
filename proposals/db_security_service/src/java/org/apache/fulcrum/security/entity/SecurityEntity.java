package org.apache.fulcrum.security.entity;


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


/**
 * This class represents a Group of Users in the system that are associated
 * with specific entity or resource. The users belonging to the Group may have
 * various Roles. The Permissions to perform actions upon the resource depend
 * on the Roles in the Group that they are assigned.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */

public interface SecurityEntity
{
    /**
     * Get the Name of the SecurityEntity.
     *
     * @return The Name of the SecurityEntity.
     */
    String getName();

    /**
     * Set the Name of the SecurityEntity.
     *
     * @param name Name of the SecurityEntity.
     */
    void setName(String name);
}
