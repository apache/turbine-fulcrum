package org.apache.fulcrum.intake.model;

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
import org.apache.fulcrum.intake.validator.BooleanValidator;

/**
 * Processor for boolean fields.
 *
 * @version $Id$
 */
public class BooleanField
        extends Field<Boolean>
{
    /** Serial version */
	private static final long serialVersionUID = 6689670469518374083L;

	/**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public BooleanField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for a Boolean field
     *
     * @param prop Parameter for the default values
     */
    @Override
	public void setDefaultValue(String prop)
    {
        defaultValue = null;

        if (prop == null)
        {
            return;
        }

        defaultValue = Boolean.valueOf(prop);
    }

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existant.
     *
     * @param prop The value to use if the field is empty.
     */
    @Override
	public void setEmptyValue(String prop)
    {
        emptyValue = null;

        if (prop == null)
        {
            return;
        }

        emptyValue = Boolean.valueOf(prop);
    }

    /**
     * Provides access to emptyValue such that the value returned will be
     * acceptable as an argument parameter to Method.invoke.  Subclasses
     * that deal with primitive types should ensure that they return an
     * appropriate value wrapped in the object wrapper class for the
     * primitive type.
     *
     * @return the value to use when the field is empty or an Object that
     * wraps the empty value for primitive types.
     */
    @Override
	protected Object getSafeEmptyValue()
    {
        if (isMultiValued())
        {
            return new boolean[0];
        }
        else
        {
            return (null == getEmptyValue()) ? Boolean.FALSE : getEmptyValue();
        }
    }

    /**
     * A suitable validator.
     *
     * @return class name of the validator
     */
    @Override
	protected String getDefaultValidator()
    {
        return BooleanValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    @Override
	protected void doSetValue()
    {
        if (isMultiValued())
        {
            Boolean[] inputs = parser.getBooleanObjects(getKey());
            boolean[] values = new boolean[inputs.length];

            for (int i = 0; i < inputs.length; i++)
            {
                values[i] = inputs[i] == null
                        ? getEmptyValue().booleanValue()
                        : inputs[i].booleanValue();
            }

            setTestValue(values);
        }
        else
        {
            setTestValue(parser.getBooleanObject(getKey(), getEmptyValue()));
        }
    }

    /**
     * Gets the boolean value of the field.  A value of false will be returned
     * if the value of the field is null.
     *
     * @return value of the field.
     */
    public boolean booleanValue()
    {
        boolean result = false;
        try
        {
            result = getValue().booleanValue();
        }
        catch (Exception e)
        {
            log.error("Error getting boolean value " + getValue(), e);
        }
        return result;
    }

}
