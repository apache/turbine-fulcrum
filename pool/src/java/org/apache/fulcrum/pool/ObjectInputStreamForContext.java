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


import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.IOException;

/**
 * A deserialization stream for a specific class loader context.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class ObjectInputStreamForContext extends ObjectInputStream
{
    /**
     * The class loader of the context.
     */
    private ClassLoader classLoader;

    public ObjectInputStreamForContext()
        throws IOException
    {
        // this is to make the proxy happy.
    }

    /**
     * Contructs a new object stream for a context.
     *
     * @param in the serialized input stream.
     * @param loader the class loader of the context.
     * @throws IOException on errors.
     */
    public  ObjectInputStreamForContext(InputStream in,
                                        ClassLoader loader)
                                        throws IOException
    {
        super(in);
        classLoader = loader;
    }

    protected Class resolveClass(ObjectStreamClass v)
                                 throws IOException,
                                 ClassNotFoundException
    {
        return classLoader == null ?
            super.resolveClass(v) : classLoader.loadClass(v.getName());
    }
}
