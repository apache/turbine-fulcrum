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

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.validator.StringValidator;

/**
 * Text field.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class StringField
        extends Field<String>
{
    /** Serial version */
	private static final long serialVersionUID = -7218385325093690333L;

	/**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public StringField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Produces the fully qualified class name of the default validator.
     *
     * @return class name of the default validator
     */
    @Override
	protected String getDefaultValidator()
    {
        return StringValidator.class.getName();
    }

    /**
     * Sets the default value for a String field
     *
     * @param prop Parameter for the default values
     */
    @Override
	public void setDefaultValue(String prop)
    {
        defaultValue = prop;
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
        emptyValue = prop;
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    @Override
	protected void doSetValue()
    {
        if (isMultiValued())
        {
            String[] ss = parser.getStrings(getKey());
            String[] sval = new String[ss.length];
            for (int i = 0; i < ss.length; i++)
            {
                sval[i] = StringUtils.isNotEmpty(ss[i]) ? ss[i] : (String) getEmptyValue();
            }
            setTestValue(sval);
        }
        else
        {
            String val = parser.getString(getKey());
            setTestValue(StringUtils.isNotEmpty(val) ? val : (String) getEmptyValue());
        }
    }

    /**
     * Set the value of required.
     *
     * @param v  Value to assign to required.
     * @param message an error message
     */
    @Override
	public void setRequired(boolean v, String message)
    {
        super.setRequired(v, message);
        if (v)
        {
            if (isMultiValued())
            {
                String[] ss = (String[]) getTestValue();
                if (ss == null || ss.length == 0)
                {
                    setValid(false);
                    setMessage(message);
                }
                else
                {
                    boolean set = false;
                    for (int i = 0; i < ss.length; i++)
                    {
                        set |= StringUtils.isNotEmpty(ss[i]);
                        if (set)
                        {
                            break;
                        }
                    }
                    if (!set)
                    {
                        setValid(false);
                        setMessage(message);
                    }
                }
            }
            else
            {
                if (!isSet() || StringUtils.isEmpty((String)getTestValue()))
                {
                    setValid(false);
                    setMessage(message);
                }
            }
        }
    }
}
