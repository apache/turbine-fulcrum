package org.apache.fulcrum.intake;

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
 * This interface specifies methods for uniquely identifying an object.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public interface Retrievable
{
    /**
     * get an id that differentiates this object from others
     * of its class.
     *
     * @return the unique key identifying the object
     */
    String getQueryKey();

    /**
     * set an id that differentiates this object from others
     * of its class.
     *
     * @param key the identifying key
     *
     * @throws Exception if the key could not be set
     */
    void setQueryKey(String key) throws Exception;
}
