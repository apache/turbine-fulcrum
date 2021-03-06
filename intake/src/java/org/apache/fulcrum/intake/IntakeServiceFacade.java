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

import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import org.apache.fulcrum.intake.model.Group;

/**
 * This is a Facade class for IntakeService.
 *
 * This class provides static methods that call related methods of the
 * implementation of the IntakeService used by the System, according to the
 * settings in your intake.xml file.
 *
 * Note: How should a facade class work? It seems to me that maybe it should
 * only have a hook into the Avalon Component Manager somehow?
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class IntakeServiceFacade
{
    /** Static instance of the intakeService. */
    private static IntakeService intakeService;

    /**
     * Return whether the intake service has been initialized.
     * @return true if the service has been initialized
     */
    public static boolean isInitialized()
    {
        return intakeService != null;
    }

    /**
     * Gets an instance of a named group either from the pool or by calling the
     * Factory Service if the pool is empty.
     *
     * @param groupName
     *            the name of the group.
     * @return a Group instance.
     * @throws IntakeException
     *             if recycling fails.
     */
    public static Group getGroup(String groupName) throws IntakeException
    {
        if (groupName == null)
        {
            throw new IntakeException("IntakeServiceFacade.getGroup(groupName) is null");
        }
        return getService().getGroup(groupName);
    }

    /**
     * Puts a group back to the pool.
     *
     * @param instance
     *            the object instance to recycle.
     * @throws IntakeException
     *             A non existant group was passed
     */
    public static void releaseGroup(Group instance) throws IntakeException
    {
        getService().releaseGroup(instance);
    }

    /**
     * Gets the current size of the pool for a named group.
     *
     * @param groupName
     *            the name of the group.
     * @return the current pool size
     * @throws IntakeException
     *             A non-existent group was passed
     */
    public static int getSize(String groupName) throws IntakeException
    {
        return getService().getSize(groupName);
    }

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
    public static String[] getGroupNames()
    {
        return getService().getGroupNames();
    }

    /**
     * Gets the key (usually a short identifier) for a group.
     *
     * @param groupName
     *            the name of the group.
     * @return the the key.
     */
    public static String getGroupKey(String groupName)
    {
        return getService().getGroupKey(groupName);
    }

    /**
     * Gets the group name given its key.
     *
     * @param groupKey
     *            the key.
     * @return groupName the name of the group.
     */
    public static String getGroupName(String groupKey)
    {
        return getService().getGroupName(groupKey);
    }

    /**
     * Gets the Method that can be used to set a property.
     *
     * @param className
     *            the name of the object.
     * @param propName
     *            the name of the property.
     * @return the setter.
     * @throws ClassNotFoundException if the class specified could not be loaded
     * @throws IntrospectionException if the property setter could not be called
     */
    public static Method getFieldSetter(String className, String propName) throws IntrospectionException, ClassNotFoundException
    {
        return getService().getFieldSetter(className, propName);
    }

    /**
     * Gets the Method that can be used to get a property value.
     *
     * @param className
     *            the name of the object.
     * @param propName
     *            the name of the property.
     * @return the getter.
     * @throws ClassNotFoundException if the class specified could not be loaded
     * @throws IntrospectionException if the property getter could not be called
     */
    public static Method getFieldGetter(String className, String propName) throws IntrospectionException, ClassNotFoundException
    {
        return getService().getFieldGetter(className, propName);
    }

    /**
     * Utility method for accessing the service implementation
     *
     * @return a IntakeService implementation instance
     */
    private static IntakeService getService()
    {
        return intakeService;
    }

    public static void setIntakeService(IntakeService service)
    {
        intakeService = service;
    }

}
