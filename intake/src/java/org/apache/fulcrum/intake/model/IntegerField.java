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
import org.apache.fulcrum.intake.validator.IntegerValidator;

/**
 * Processor for int fields.
 *
 * @version $Id$
 */
public class IntegerField
        extends Field<Integer>
{
    /** Serial version */
	private static final long serialVersionUID = -4186153269451228653L;

	/**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public IntegerField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for an Integer Field
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

        defaultValue = Integer.valueOf(prop);
    }

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existent.
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

        emptyValue = Integer.valueOf(prop);
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
            return new int[0];
        }
        else
        {
            return (null == getEmptyValue())
                    ? Integer.valueOf(0) : getEmptyValue();
        }
    }

    /**
     * A suitable validator.
     *
     * @return A suitable validator
     */
    @Override
	protected String getDefaultValidator()
    {
        return IntegerValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    @Override
	protected void doSetValue()
    {
        if (isMultiValued())
        {
            Integer[] inputs = parser.getIntObjects(getKey());
            int[] values = new int[inputs.length];

            for (int i = 0; i < inputs.length; i++)
            {
                values[i] = inputs[i] == null
                        ? getEmptyValue().intValue()
                        : inputs[i].intValue();
            }

            setTestValue(values);
        }
        else
        {
            setTestValue(parser.getIntObject(getKey(), getEmptyValue()));
        }
    }

}
