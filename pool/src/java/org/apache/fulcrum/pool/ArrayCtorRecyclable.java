package org.apache.fulcrum.pool;


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
 * An interface for objects that can be pooled and
 * recycled several times by different clients.  This interface
 * presents a recycle method that does not require introspection/reflection.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public interface ArrayCtorRecyclable extends Recyclable
{
    /**
     * Recycles the object for a new client. Objects implementing
     * this interface must also provide a matching constructor.
     * The recycle methods must call their super.
     * 
     * @param params the parameters to recycle
     */
    public void recycle(Object[] params);
}
