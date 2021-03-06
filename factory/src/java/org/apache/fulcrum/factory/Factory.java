package org.apache.fulcrum.factory;

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
 * Factory is an interface for object factories. Object factories
 * can be registered with the Factory Service to support customized
 * functionality during instantiation of specific classes that
 * the service itself cannot provide. Examples include
 * instantiation of XML parsers and secure sockets requiring
 * provider specific initializations before instantiation.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 */
public interface Factory<T>
{
    /**
     * Initializes the factory. This method is called by
     * the Factory Service before the factory is used.
     *
     * @param className the name of the production class
     * @throws FactoryException if initialization fails.
     */
    void init(String className) throws FactoryException;

    /**
     * Gets an instance of a class.
     *
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    T getInstance() throws FactoryException;

    /**
     * Gets an instance of a class using a specified class loader.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param loader the class loader.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    T getInstance(ClassLoader loader) throws FactoryException;

    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    T getInstance(Object[] params, String[] signature)
        throws FactoryException;

    /**
     * Gets an instance of a named class using a specified class loader.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws FactoryException if instantiation fails.
     */
    T getInstance(ClassLoader loader, Object[] params, String[] signature)
        throws FactoryException;

    /**
     * Tests if this object factory supports specified class loaders.
     *
     * @return true if class loaders are supported, false otherwise.
     */
    boolean isLoaderSupported();
}
