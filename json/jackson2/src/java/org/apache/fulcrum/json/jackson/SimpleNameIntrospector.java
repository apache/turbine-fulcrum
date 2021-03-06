package org.apache.fulcrum.json.jackson;

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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * The intent of this custom introspector is to provide filtering capabilities
 * by using String parameters (properties and class types), which could be
 * adjusted e.g. from a scriptable context (velocity template). 
 * Class Type Filtering currently not supported except for Exclude Filter: {@link Jackson2MapperService#serializeAllExceptFilter(Object, Class, Boolean, String...)}.
 * 
 * 
 * @author gk
 * @version $Id$
 * 
 */
public class SimpleNameIntrospector extends NopAnnotationIntrospector implements LogEnabled {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<Class<?>> filteredClasses = new CopyOnWriteArrayList<>();
    private List<String> externalFilterExcludeClasses = new CopyOnWriteArrayList<>();
    private List<String> externalFilterIncludeClasses = new CopyOnWriteArrayList<>();
    // is used only for filtering by class
    private AtomicBoolean isExludeType = new AtomicBoolean(false);
    
    private static Logger logger;

    /**
     * Filtering on method types.
     * 
     */
    @Override
    public Boolean isIgnorableType(AnnotatedClass ac) {
        Boolean isIgnorable = super.isIgnorableType(ac);
        if (isIgnorable == null || !isIgnorable) {
            if (getIsExludeType()) { // could be removed, if cleaning after call ?
                if (!externalFilterExcludeClasses.isEmpty()
                        && externalFilterExcludeClasses.contains(ac.getName())) {
                    isIgnorable = true;
                }
            } else {
                // not yet used
                if (!externalFilterIncludeClasses.isEmpty()
                        && !externalFilterIncludeClasses.contains(ac.getName())) {
                        try {
                            Class.forName(ac.getName());
                            isIgnorable = true;
                        } catch (ClassNotFoundException e) {
                            // no clazz ignore, could NOT ignore as no filterable clazz
                        }
                }
            }
        }
        return isIgnorable;
    }
    /**
     * @return Object Filtering on properties returns an object, if
     *         {@link #filteredClasses} contains the class provided. The
     *         filter itself currently is {@link SimpleFilterProvider}.
     */
    @Override
    public Object findFilterId(Annotated ac) {
        Object id = super.findFilterId(ac);
        // Let's default to current behavior if annotation is found:
        // Object id = super.findFilterId(ac);
        // but use simple class name if not
        if (id == null) {
            String name = ac.getName();
            Class<?> targetClazz = ac.getRawType();
            if (!filteredClasses.isEmpty()
                    && filteredClasses.contains(targetClazz)
                    ) {
                logger.debug("filter applying to " +name);
                id = name;
            } else {
                // check if target class is a child from filter class -> apply filter 
                for (Class<?> filterClazz : filteredClasses) {
                    // the currently checked instance of type targetClazz is a child of the filter class filterClazz ->  filter child 
                    if (filterClazz.isAssignableFrom(targetClazz)) {
                        logger.debug("filter applying to parent " +filterClazz +" matching child class "+name );
                        id = name;
                        break;
                    }
                    // the currently checked instance of type targetClazz is a parent of the filter class filterClazz -> filter parent
                    if (targetClazz.isAssignableFrom(filterClazz)) {
                        logger.debug("filter applying to child " +filterClazz+" matching parent class "+name);
                        id = name;
                        break;
                    }
                }
            }
        }
        return id;
    }

    public List<Class<?>> getFilteredClasses() {
        return filteredClasses;
    }

    public void setFilteredClass(Class<?> filteredClass) {
        if (!filteredClasses.contains(filteredClass)) {
            filteredClasses.add(filteredClass);
        }
    }

    public void setFilteredClasses(Class<?>... classes) {

        for (int i = 0; i < classes.length; i++) {
            if (!filteredClasses.contains(classes[i])) {
                filteredClasses.add(classes[i]);
            }
//            if (classes[i].getSuperclass() != null) {
//                Class superClazz = classes[i].getSuperclass();
//                if (!externalFilterClasses.contains(superClazz)) {
//                    externalFilterClasses.add(superClazz);
//                }  
//            }
        }
    }

    public void removeFilteredClass(Class<?> filteredClass) {
            if (filteredClasses.contains(filteredClass)) {
                filteredClasses.remove(filteredClass);
            }
    }
    
    public void setExternalFilterExcludeClasses(Class<?>... classes) {

        for (int i = 0; i < classes.length; i++) {
            if (!externalFilterExcludeClasses.contains(classes[i].getName())) {

                externalFilterExcludeClasses.add(classes[i].getName());
            }
        }
    }
    
    public void removeExternalFilterExcludeClass(Class<?> externalFilterClass) {
        if (externalFilterExcludeClasses.contains(externalFilterClass.getName())) {
            externalFilterExcludeClasses.remove(externalFilterClass.getName());
        }
    }
    
    public void setExternalFilterIncludeClasses(Class<?>... classes) {

        for (int i = 0; i < classes.length; i++) {
            if (!externalFilterIncludeClasses.contains(classes[i].getName())) {

                externalFilterIncludeClasses.add(classes[i].getName());
            }
        }
    }
    
    public void removeExternalFilterIncludeClasses(Class<?> externalFilterClass) {
        if (externalFilterIncludeClasses.contains(externalFilterClass.getName())) {
            externalFilterIncludeClasses.remove(externalFilterClass.getName());
        }
    }
    
    public boolean getIsExludeType() {
        return isExludeType.get();
    }
    public void setIsExludeType(boolean isExludeType) {
        this.isExludeType.getAndSet(isExludeType);
    }
    @Override
    public void enableLogging(Logger logger) {
        SimpleNameIntrospector.logger = logger;        
    }

}