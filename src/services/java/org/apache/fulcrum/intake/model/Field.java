package org.apache.fulcrum.intake.model;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Map;
import java.lang.reflect.Method;
import org.apache.regexp.RE;
import org.apache.fulcrum.util.parser.ValueParser;
import org.apache.fulcrum.intake.Retrievable;
import org.apache.fulcrum.intake.TurbineIntake;
import org.apache.fulcrum.intake.xmlmodel.Rule;
import org.apache.fulcrum.intake.xmlmodel.XmlField;
import org.apache.fulcrum.intake.xmlmodel.XmlGroup;
import org.apache.fulcrum.intake.validator.Validator;
import org.apache.fulcrum.intake.validator.InitableByConstraintMap;
import org.apache.fulcrum.intake.validator.ValidationException;
import org.apache.fulcrum.ServiceException;
import org.apache.log4j.Category;

/**
 * Base class for Intake generated input processing classes.
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @version $Id$
 */
public abstract class Field
{
    private static final String EMPTY = "";
    private static final String VALUE_IF_ABSENT_KEY = "_vifa_";

    // the following are set from the xml file and are permanent (final)
    protected final String name;
    protected final String key;
    protected String displayName;
    protected final String mapToObject;
    protected Validator validator;
    protected final Method getter;
    protected final Method setter;
    protected final String ifRequiredMessage;
    protected final boolean isMultiValued;
    protected final Group group;
    protected boolean alwaysRequired;
    protected Object onError;

    // these are reset when the Field is returned to the pool
    protected boolean set_flag;
    protected boolean valid_flag;
    protected boolean required;
    protected boolean initialized;
    protected String message;
    protected Retrievable retrievable;

    private String stringValue;
    private String[] stringValues;
    private Object validValue;
    private Object testValue;
    private Object[] valArray; // for reflection

    /** The object containing the request data */
    protected ValueParser pp;

    /**
     * Log4j category
     */
    Category category = Category.getInstance(getClass().getName());

    /**
     * Constructs a field based on data in the xml specification
     * and assigns it to a Group.
     *
     * @param field a <code>XmlField</code> value
     * @param group a <code>Group</code> value
     * @exception Exception if an error occurs
     */
    public Field(XmlField field, Group group)
        throws Exception
    {
        this.group = group;
        key = field.getKey();
        name = field.getName();
        displayName = field.getDisplayName();
        isMultiValued  = field.isMultiValued();
        String className = field.getValidator();
        if ( className == null && field.getRules().size() > 0 )
        {
            className = getDefaultValidator();
        }
        else if ( className != null && className.indexOf('.') == -1 )
        {
            className = "org.apache.fulcrum.intake.validator."
                + className;
        }

        if ( className != null )
        {
            validator = (Validator)Class.forName(className).newInstance();
            // this should always be true for now
            // (until bean property initialization is implemented)
            if ( validator instanceof InitableByConstraintMap )
            {
                ((InitableByConstraintMap)validator).init(field.getRuleMap());
            }

        }

        // field may have been declared as always required in the xml spec
        Rule reqRule = (Rule)field.getRuleMap().get("required");
        if ( reqRule != null )
        {
            alwaysRequired = new Boolean(reqRule.getValue()).booleanValue();
        }

        mapToObject = field.getMapToObject();
        String propName = field.getMapToProperty();
        Method tmpGetter = null;
        Method tmpSetter = null;
        if ( mapToObject != null && mapToObject.length() != 0)
        {
            tmpGetter = TurbineIntake.getFieldGetter(mapToObject, propName);
            tmpSetter = TurbineIntake.getFieldSetter(mapToObject, propName);
        }
        getter = tmpGetter;
        setter = tmpSetter;
        ifRequiredMessage = field.getIfRequiredMessage();

        valArray = new Object[1];
    }


    /**
     * Method called when this field (the group it belongs to) is
     * pulled from the pool.  The request data is searched to determine
     * if a value has been supplied for this field.  if so, the value
     * is validated.
     *
     * @param pp a <code>ValueParser</code> value
     * @return a <code>Field</code> value
     * @exception ServiceException if an error occurs
     */
    public Field init(ValueParser pp)
        throws ServiceException
    {
        this.pp = pp;
        valid_flag = true;

        if ( pp.containsKey(getKey()) && pp.getString(getKey()) != null )
        {
            set_flag = true;
            if (validate(pp))
            {
                // iv.reconcileNotValid(pp);
            }
        }
        else if ( pp.containsKey(getValueIfAbsent()) &&
                  pp.getString(getValueIfAbsent()) != null )
        {
            pp.add(getKey(), pp.getString(getValueIfAbsent()));
            set_flag = true;
            validate(pp);
        }

        initialized = true;
        return this;
    }

    /**
     * Method called when this field or the group it belongs to is
     * pulled from the pool.  The retrievable object can provide
     * a default value for the field, or using setProperty the field's
     * value can be transferred to the retrievable.
     *
     * @param obj a <code>Retrievable</code> value
     * @return a <code>Field</code> value
     */
    public Field init(Retrievable obj)
    {
        if ( !initialized )
        {
            valid_flag = true;
        }
        retrievable = obj;
        return this;
    }


    protected String getDefaultValidator()
    {
        return "org.apache.fulcrum.intake.validator.DefaultValidator";
    }

    public Validator getValidator()
    {
        return validator;
    }

    /**
     * Flag to determine whether the field has been declared as required.
     * @return value of required.
     */
    public boolean isRequired()
    {
        return alwaysRequired || required;
    }

    /**
     * Set whether this field is required to have a value.
     * @param v  Value to assign to required.
     */
    public void setRequired(boolean  v)
    {
        setRequired(v, ifRequiredMessage);
    }

    /**
     * Set the value of required.
     *
     * @param v a <code>boolean</code> value
     * @param message, override the value from intake.xml
     */
    public void setRequired(boolean  v, String message)
    {
        this.required = v;
        if (v && !set_flag)
        {
            valid_flag=false;
            this.message = message;
        }
    }

    /**
     * Removes references to this group and its fields from the
     * query parameters
     */
    public void removeFromRequest()
    {
        pp.remove(getKey());
    }


    /**
     * Disposes the object after use. The method is called
     * when the Group is returned to its pool.
     * if overridden, super.dispose() should be called.
     */
    public void dispose()
    {
        pp = null;
        initialized = false;
        set_flag = false;
        valid_flag = false;
        required = false;
        message = null;
        retrievable = null;

        stringValue = null;
        stringValues = null;
        validValue = null;
        testValue = null;
        valArray[0] = null;
    }

    /**
     * Get the key used to identify the field.
     * @return the query data key.
     */
    public String getKey()
    {
        if ( group == null )
        {
            return key;
        }
        else
        {
            return group.getObjectKey() + key;
        }
    }

    /**
     * Use in a hidden field assign a default value in the event the
     * field is absent from the query parameters.  Used to track checkboxes,
     * since they only show up if checked.
     */
    public String getValueIfAbsent()
    {
        return getKey() + VALUE_IF_ABSENT_KEY;
    }

    /**
     * Flag set to true, if the test value met the constraints.
     * Is also true, in the case the test value was not set,
     * unless this field has been marked as required.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isValid()
    {
        return valid_flag;
    }

    /**
     * Flag set to true, if the test value has been set to
     * anything other than an empty value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isSet()
    {
        return set_flag;
    }

    /**
     * Get the display name of the field. Useful for building
     * data entry forms. Returns name of field if no display
     * name has been assigned to the field by xml input file
     *
     * @return a <code>String</code> value
     */
    public String getDisplayName()
    {
        return (displayName == null) ? name : displayName;
    }

    /**
     * Set the display name of the field. Display names are
     * used in building data entry forms and serve as a
     * user friendly description of the data contained in
     * the field.
     */
    public void setDisplayName(String newDisplayName)
    {
        displayName = newDisplayName;
    }

    /**
     * Get any error message resulting from invalid input.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        if ( message == null )
        {
            return EMPTY;
        }
        return message;
    }

    /**
     * Sets an error message.  The field is also marked as invalid.
     */
    public void setMessage(String message)
    {
         this.message = message;
         valid_flag = false;
    }

    /**
     * Compares request data with constraints and sets the valid flag.
     */
    protected boolean validate(ValueParser pp)
    //    throws ServiceException
    {
        if ( isMultiValued  )
        {
            stringValues = pp.getStrings(getKey());
            // this definition of not set might need refined.  But
            // not sure the situation will arise.
            if ( stringValues.length == 0 || 
                 (stringValues.length == 1 && stringValues[0].length() == 0) )
            {
                set_flag = false;
            }

            if ( validator != null )
            {
                for (int i=0; i<stringValues.length; i++)
                {
                    try
                    {
                        validator.assertValidity(stringValues[i]);
                    }
                    catch (ValidationException ve)
                    {
                        setMessage(ve.getMessage());
                    }
                }
            }

            if ( set_flag && valid_flag )
            {
                doSetValue(pp);
            }

        }
        else
        {
            stringValue = pp.getString(getKey());
            if ( stringValue.length() == 0 )
            {
                set_flag = false;
            }

            if ( validator != null )
            {
                try
                {
                    validator.assertValidity(stringValue);

                    if ( set_flag )
                    {
                        doSetValue(pp);
                    }
                }
                catch (ValidationException ve)
                {
                    setMessage(ve.getMessage());
                }
            }
            else if ( set_flag )
            {
                doSetValue(pp);
            }
        }

        return valid_flag;
    }

    /**
     * Compares request data with constraints and sets the valid flag.
     * To be implemented in subclasses
     */
    protected abstract void doSetValue(ValueParser pp);



    /**
     * Set the value used as a default, in the event the field
     * has not been set yet.
     *
     * @param obj an <code>Object</code> value
     */
    void setInitialValue(Object obj)
    {
        validValue = obj;
    }

    /**
     * Get the value used as a default.  If the initial value has
     * not been set and a <code>Retrievable</code> object has
     * been associated with this field, the objects property will
     * be used as the initial value.
     *
     * @return an <code>Object</code> value
     * @exception Exception if an error occurs
     */
    public Object getInitialValue()
        throws Exception
    {
        if ( validValue == null)
        {
            if ( retrievable != null )
            {
                getProperty(retrievable);
            }
        }
        return validValue;
    }

    /**
     * Set the value input by a user that will be validated.
     *
     * @param obj an <code>Object</code> value
     */
    void setTestValue(Object obj)
    {
        testValue = obj;
    }

    /**
     * Get the value input by a user that will be validated.
     *
     * @return an <code>Object</code> value
     */
    public Object getTestValue()
    {
        return testValue;
    }

    /**
     * Get the value of the field.  if a test value has been set, it
     * will be returned as is, unless it is so badly formed that the
     * validation could not parse it.  In most cases the test value
     * is returned even though invalid, so that it can be returned to
     * the user to make modifications.  if the test value is not set
     * the initial value is returned.
     *
     * @return an <code>Object</code> value
     */
    public Object getValue()
    {
        Object val = null;
        try
        {
            val = getInitialValue();
        }
        catch (Exception e)
        {
            category.error(e);
        }

        if ( getTestValue() != null )
        {
            val = getTestValue();
        }

        if ( val == null )
        {
            val = onError;
        }
        return val;
    }

    /**
     * Calls toString() on the object returned by getValue(),
     * unless null; and then it returns "", the empty String.
     *
     * @return a <code>String</code> value
     */
    public String toString()
    {
        if ( stringValue != null ) 
        {
            return stringValue;
        }
        else if ( getValue() != null )
        {
            return getValue().toString();
        }
        else
        {
            return EMPTY;
        }
    }

    /**
     * Loads the valid value from a bean
     */
    public void getProperty(Object obj)
        throws Exception
    {
        validValue = getter.invoke(obj, null);
    }

    /**
     * Calls a setter method on obj, if this field has been set.
     * @exception throws a ServiceException if called and the input
     * was not valid.
     */
    public void setProperty(Object obj)
    // public void setProperty($appData.BasePackage$field.MapToObject obj)
        throws ServiceException
    {
        if (!isValid())
        {
            throw new ServiceException(
                "Attempted to assign an invalid input.");
        }
        if (isSet())
        {
            try
            {
                valArray[0] = getTestValue();
                setter.invoke(obj, valArray);
            }
            catch ( Exception e)
            {
                throw new ServiceException("An exception prevented the" +
                    " setting property "+name+" of " + obj + " to " +
                                           valArray[0], e);
            }
        }
    }
}
