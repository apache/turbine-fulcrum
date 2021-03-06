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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.intake.IntakeError;
import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.IntakeServiceFacade;
import org.apache.fulcrum.intake.Retrievable;
import org.apache.fulcrum.intake.validator.DefaultValidator;
import org.apache.fulcrum.intake.validator.InitableByConstraintMap;
import org.apache.fulcrum.intake.validator.ValidationException;
import org.apache.fulcrum.intake.validator.Validator;
import org.apache.fulcrum.parser.ValueParser;

/**
 * Base class for Intake generated input processing classes.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public abstract class Field<T> implements Serializable, LogEnabled
{
    /** Serial version */
    private static final long serialVersionUID = 6897267716698096895L;

    /** Empty Value */
    private static final String EMPTY = "";

    /** CGI Key for "value if absent" */
    private static final String VALUE_IF_ABSENT_KEY = "_vifa_";

    /** Default Validator Package */
    public static final String defaultValidatorPackage = "org.apache.fulcrum.intake.validator.";

    /** Default Field Package */
    public static final String defaultFieldPackage = "org.apache.fulcrum.intake.model.";

    // the following are set from the xml file and are permanent (final)

    /** Name of the field. */
    private final String name;

    /** Key used to identify the field in the parser */
    private final String key;

    /** Display size of the field */
    private final String displaySize;

    /** Class name of the object to which the field is mapped */
    protected String mapToObject;

    /** Optional property name of the object to which the field is mapped */
    private String mapToProperty;

    /** Class name of the validator (for deserialization) */
    private String validatorClassName;

    /** Used to validate the contents of the field */
    private transient Validator<T> validator;

    /** Getter method in the mapped object used to populate the field */
    private Method getter;

    /** Setter method in the mapped object used to store the value of field */
    private Method setter;

    /** Error message set on the field if required and not set by parser */
    private String ifRequiredMessage;

    /** Does this field accept multiple values? */
    private final boolean isMultiValued;

    /** Group to which the field belongs */
    private final Group group;

    /** Is this field always required?  This is only set through the XML file */
    private boolean alwaysRequired;

    /** Default value of the field */
    protected T defaultValue;

    /** Value of the field to use if the mapped parameter is empty or non-existent */
    protected T emptyValue;

    /** Display name of the field to be used on data entry forms... */
    private String displayName;

    /** Max size of the field */
    private String maxSize;

    // these are reset when the Field is returned to the pool

    /** Has the field has been set from the parser? */
    private boolean setFlag;

    /** Has the field passed the validation test? */
    private boolean validFlag;

    /** Has the field been validated? */
    private boolean validated;

    /** Does the field require a value? */
    private boolean required;

    /** Has the field has been set from the parser? */
    private boolean initialized;

    /** Error message, is any, resulting from validation */
    private String message;

    /** Mapped object used to set the initial field value */
    private Retrievable retrievable;

    /** Locale of the field */
    private Locale locale;

    /** String value of the field */
    private String stringValue;

    /** String values of the field if isMultiValued=true */
    private String[] stringValues;

    /** Stores the value of the field from the Retrievable object */
    private T validValue;

    /** Stores the value of the field from the parser */
    private Object testValue;

    /** Used to pass testValue to the setter method through reflection */
    private final Object[] valArray;

    /** The object containing the field data. */
    protected ValueParser parser;

    /** Store rules for deserialization */
    private Map<String, Rule> ruleMap;

    /** Logging */
    protected transient Logger log;

    /**
     * Constructs a field based on data in the xml specification
     * and assigns it to a Group.
     *
     * @param field a <code>XmlField</code> value
     * @param group a <code>Group</code> value
     * @throws IntakeException indicates the validator was not valid or
     * could not be loaded.
     */
    public Field(XmlField field, Group group) throws IntakeException
    {
    	enableLogging(field.getLogger());
        this.group = group;
        key = field.getKey();
        name = field.getName();
        displayName = field.getDisplayName();
        displaySize = field.getDisplaySize();
        isMultiValued = field.isMultiValued();
        ruleMap = field.getRuleMap();

        try
        {
            setDefaultValue(field.getDefaultValue());
        }
        catch (RuntimeException e)
        {
            log.error("Could not set default value of " +
                    this.getDisplayName() + " to "
                    + field.getDefaultValue(), e);
        }

        try
        {
            setEmptyValue(field.getEmptyValue());
        }
        catch (RuntimeException e)
        {
            log.error("Could not set empty value of " +
                    this.getDisplayName() + " to "
                    + field.getEmptyValue(), e);
        }

        this.validatorClassName = field.getValidator();
        if (validatorClassName == null)
        {
            validatorClassName = getDefaultValidator();
        }
        else if (validatorClassName.indexOf('.') == -1)
        {
            validatorClassName = defaultValidatorPackage + validatorClassName;
        }

        // field may have been declared as always required in the xml spec
        Rule reqRule = field.getRuleMap().get(Validator.REQUIRED_RULE_NAME);
        if (reqRule != null)
        {
            alwaysRequired = Boolean.valueOf(reqRule.getValue()).booleanValue();
            ifRequiredMessage = reqRule.getMessage();
        }

        Rule maxLengthRule = field.getRuleMap().get(Validator.MAX_LENGTH_RULE_NAME);
        if (maxLengthRule != null)
        {
            maxSize = maxLengthRule.getValue();
        }

        // map the getter and setter methods
        mapToObject = field.getMapToObject();
        mapToProperty = field.getMapToProperty();
        valArray = new Object[1];
    }

    /**
	 * Enable Avalon Logging
	 */
	@Override
	public void enableLogging(Logger logger)
	{
		this.log = logger.getChildLogger(getClass().getSimpleName());
	}

    /**
     * Initialize getter and setter from properties
     */
    public void initGetterAndSetter()
    {
        Method tmpGetter = null;
        Method tmpSetter = null;
        if (StringUtils.isNotEmpty(mapToObject)
                && StringUtils.isNotEmpty(mapToProperty))
        {
            try
            {
                tmpGetter = IntakeServiceFacade.getFieldGetter(mapToObject, mapToProperty);
            }
            catch (Exception e)
            {
                log.error("IntakeService could not map the getter for field "
                        + this.getDisplayName() + " in group "
                        + this.group.getIntakeGroupName()
                        + " to the property " + mapToProperty + " in object "
                        + mapToObject, e);
            }
            try
            {
                tmpSetter = IntakeServiceFacade.getFieldSetter(mapToObject, mapToProperty);
            }
            catch (Exception e)
            {
                log.error("IntakeService could not map the setter for field "
                        + this.getDisplayName() + " in group "
                        + this.group.getIntakeGroupName()
                        + " to the property " + mapToProperty + " in object "
                        + mapToObject, e);
            }
        }
        getter = tmpGetter;
        setter = tmpSetter;
    }

    /**
     * Method called when this field (the group it belongs to) is
     * pulled from the pool.  The request data is searched to determine
     * if a value has been supplied for this field.  If so, the value
     * is validated.
     *
     * @param pp a <code>ValueParser</code> value
     * @return a <code>Field</code> value
     * @throws IntakeException this exception is only thrown by subclasses
     * overriding this implementation.
     */
    public Field<T> init(ValueParser pp)
            throws IntakeException
    {
        this.parser = pp;
        setValid(true);
        setValidated(false);

        this.locale = pp.getLocale();

        if (pp.containsKey(getKey()))
        {
            if (log.isDebugEnabled())
            {
                log.debug(name + ": Found our Key in the request, setting Value");
            }
            if (pp.getString(getKey()) != null)
            {
                setFlag = true;
            }
            // validate();
        }
        else if (pp.containsKey(getValueIfAbsent()) &&
                pp.getString(getValueIfAbsent()) != null)
        {
            pp.add(getKey(), pp.getString(getValueIfAbsent()));
            setFlag = true;
            // validate();
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
    public Field<T> init(Retrievable obj)
    {
        if (!initialized)
        {
            validFlag = true;
            validated = false;
        }
        retrievable = obj;
        return this;
    }

    /**
     * Returns the <code>Group</code> this field belongs to
     * or <code>null</code> if unknown.
     *
     * @return The group this field belongs to.
     */
    public Group getGroup()
    {
        return group;
    }

    /**
     * Returns the <code>Locale</code> used when localizing data for
     * this field, or <code>null</code> if unknown.
     *
     * @return Where to localize for.
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Produces the fully qualified class name of the default validator.
     *
     * @return class name of the default validator
     */
    protected String getDefaultValidator()
    {
        return DefaultValidator.class.getName();
    }

    /**
     * Gets the Validator object for this field.
     * @return a <code>Validator</code> object
     */
    public Validator<T> getValidator()
    {
    	if (validator == null && validatorClassName != null)
        {
            try
            {
				validator = createValidator(validatorClassName);
			}
            catch (IntakeException e)
            {
            	log.error("Could not create validator", e);
			}
        }
        return validator;
    }

    /**
     * Get the name of the object that takes this input
     *
     * @return the name of the mapped object
     */
    public String getMapToObject()
    {
        return mapToObject;
    }

    /**
     * Flag to determine whether the field has been declared as multi-valued.
     *
     * @return value of isMultiValued.
     */
    public boolean isMultiValued()
    {
        return isMultiValued;
    }

    /**
     * Flag to determine whether the field has been declared as required.
     *
     * @return value of required.
     */
    public boolean isRequired()
    {
        return alwaysRequired || required;
    }

    /**
     * Set whether this field is required to have a value.  If the field
     * is already required due to a setting in the XML file, this method
     * can not set it to false.
     *
     * @param v  Value to assign to required.
     */
    public void setRequired(boolean v)
    {
        setRequired(v, ifRequiredMessage);
    }

    /**
     * Set the value of required.
     *
     * @param v a <code>boolean</code> value
     * @param message override the value from intake.xml
     */
    public void setRequired(boolean v, String message)
    {
        this.required = v;
        if (v && (!setFlag || null == getTestValue()))
        {
            validFlag = false;
            this.message = message;
        }
    }

    /**
     * Removes references to this group and its fields from the
     * query parameters
     */
    public void removeFromRequest()
    {
        parser.remove(getKey());
        parser.remove(getKey()+ VALUE_IF_ABSENT_KEY);
    }

    /**
     * Disposes the object after use. The method is called
     * when the Group is returned to its pool.
     * if overridden, super.dispose() should be called.
     */
    public void dispose()
    {
        parser = null;
        initialized = false;
        setFlag = false;
        validFlag = false;
        validated = false;
        required = false;
        message = null;
        retrievable = null;

        locale = null;
        stringValue = null;
        stringValues = null;
        validValue = null;
        testValue = null;
        valArray[0] = null;
    }

    /**
     * Get the key used to identify the field.
     *
     * @return the query data key.
     */
    public String getKey()
    {
        return (group == null) ? key : group.getObjectKey() + key;
    }

    /**
     * Use in a hidden field assign a default value in the event the
     * field is absent from the query parameters.  Used to track checkboxes,
     * since they only show up if checked.
     *
     * @return the value if not in the request
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
        return validFlag;
    }

    /**
     * Flag to determine whether the field has been validated.
     *
     * @return value of validated.
     */
    public boolean isValidated()
    {
        return validated;
    }

    /**
     * Flag set to true, if the test value has been set by the parser (even to
     * an empty value, so don't used this to determine if the field contains a
     * non-empty value).  Validation will only be executed for fields that have
     * been set in this manner.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isSet()
    {
        return setFlag;
    }

    /**
     * Get the display name of the field. Useful for building
     * data entry forms. Returns name of field if no display
     * name has been assigned to the field by xml input file.
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
     *
     * @param newDisplayName the new display name for the field
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
        return (message == null) ? EMPTY : message;
    }

    /**
     * Sets an error message.  The field is also marked as invalid.
     *
     * @param message the new error message
     */
    public void setMessage(String message)
    {
        this.message = message;
        validFlag = false;
    }

    /**
     * Set the internal flag that the field has been set
     *
     * @param setFlag the setFlag to set
     */
    protected void setSet(boolean setFlag)
    {
        this.setFlag = setFlag;
    }

    /**
     * Set the internal flag that the field is valid
     *
     * @param validFlag the validFlag to set
     */
    protected void setValid(boolean validFlag)
    {
        this.validFlag = validFlag;
    }

    /**
     * Set the internal flag that the field has been validated
     *
     * @param validated the validated to set
     */
    protected void setValidated(boolean validated)
    {
        this.validated = validated;
    }

    /**
     * Compares request data with constraints and sets the valid flag.
     *
     * @return true if the validation succeeded
     */
    public boolean validate()
    {
        log.debug(name + ": validate()");
        Validator<T> v = getValidator();

        if (isMultiValued())
        {
            stringValues = parser.getStrings(getKey());

            if (log.isDebugEnabled())
            {
                log.debug(name + ": Multi-Valued, Value is " + stringValue);
                if (stringValues != null)
                {
                    for (int i = 0; i < stringValues.length; i++)
                    {
                        log.debug(name + ": " + i + ". Value: " + stringValues[i]);
                    }
                }
            }

            if (v != null)
            {
                // set the test value as a String[] which might be replaced by
                // the correct type if the input is valid.
                setTestValue(stringValues);

                try
                {
                    v.assertValidity(this);
                }
                catch (ValidationException ve)
                {
                    setMessage(ve.getMessage());
                }
            }

            if (validFlag)
            {
                doSetValue();
            }
        }
        else
        {
            stringValue = parser.getString(getKey());

            if (log.isDebugEnabled())
            {
                log.debug(name + ": Single Valued, Value is " + stringValue);
            }

            if (v != null)
            {
                // set the test value as a String which might be replaced by
                // the correct type if the input is valid.
                setTestValue(stringValue);

                try
                {
                    v.assertValidity(this);
                    log.debug(name + ": Value is ok");
                    doSetValue();
                }
                catch (ValidationException ve)
                {
                    log.debug(name + ": Value failed validation!");
                    setMessage(ve.getMessage());
                }
            }
            else
            {
                doSetValue();
            }
        }

        validated = true;

        return validFlag;
    }

    /**
     * Set the default Value. This value is used if
     * Intake should map this field to a new object.
     *
     * @param prop The value to use if the field is mapped to a new object.
     */
    public abstract void setDefaultValue(String prop);

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existent.
     *
     * @param prop The value to use if the field is empty.
     */
    public abstract void setEmptyValue(String prop);

    /**
     * Sets the value of the field from data in the parser.
     */
    protected abstract void doSetValue();

    /**
     * Set the value used as a default, in the event the field
     * has not been set yet.
     *
     * @param obj an <code>Object</code> value
     */
    void setInitialValue(T obj)
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
     * @throws IntakeException indicates the value could not be
     * returned from the mapped object
     */
    public T getInitialValue() throws IntakeException
    {
        if (validValue == null)
        {
            if (retrievable != null)
            {
                getProperty(retrievable);
            }
            else
            {
                getDefault();
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
     * @param <TT> the type of the test value
     * @return an <code>TT</code> value
     */
    @SuppressWarnings("unchecked")
	public <TT> TT getTestValue()
    {
        return (TT)testValue;
    }

    /**
     * Get the value of the field.  if a test value has been set, it
     * will be returned as is, unless it is so badly formed that the
     * validation could not parse it.  In most cases the test value
     * is returned even though invalid, so that it can be returned to
     * the user to make modifications.  If the test value is not set
     * the initial value is returned.
     *
     * @return an <code>Object</code> value
     */
    public T getValue()
    {
        T val = null;
        try
        {
            val = getInitialValue();
        }
        catch (IntakeException e)
        {
            log.error("Could not get intial value of " + this.getDisplayName() +
                    " in group " + this.group.getIntakeGroupName(), e);
        }

        if (getTestValue() != null)
        {
            val = getTestValue();
        }

        return val;
    }

    /**
     * Calls toString() on the object returned by getValue(),
     * unless null; and then it returns "", the empty String.
     *
     * @return a <code>String</code> value
     */
    @Override
    public String toString()
    {
        String res = EMPTY;

        if (stringValue != null)
        {
            res = stringValue;
        }
        else if (getValue() != null)
        {
            res = getValue().toString();
        }
        return res;
    }

    /**
     * Calls toString() on the object returned by getValue(),
     * unless null; and then it returns "", the empty String.
     * Escapes &quot; characters to be able to display these
     * in HTML form fields.
     *
     * @return a <code>String</code> value
     */
    public String getHTMLString()
    {
        String res = toString();
        return StringUtils.replace(res, "\"", "&quot;");
    }

    /**
     * Loads the valid value from a bean
     *
     * @param obj the object whose getter to call
     *
     * @throws IntakeException indicates a problem during the execution of the
     * object's getter method
     */
    public void getProperty(Object obj)
            throws IntakeException
    {
        try
        {
            @SuppressWarnings("unchecked") // invoke returns Object
			T t = (T)getter.invoke(obj);
			validValue = t;
        }
        catch (IllegalAccessException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (InvocationTargetException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
    }

    /**
     * Loads the default value from the object
     */
    public void getDefault()
    {
        validValue = getDefaultValue();
    }

    /**
     * Calls a setter method on obj, if this field has been set.
     *
     * @param obj the object whose setter to call
     *
     * @throws IntakeException indicates a problem during the execution of the
     * object's setter method
     */
    public void setProperty(Object obj) throws IntakeException
    {
        if (log.isDebugEnabled())
        {
            log.debug(name + ".setProperty(" + obj.getClass().getName() + ")");
        }

        if (!isValid())
        {
            throw new IntakeException(
                    "Attempted to assign an invalid input.");
        }
        if (isSet() && null != getTestValue())
        {
            valArray[0] = getTestValue();
            if (log.isDebugEnabled())
            {
                log.debug(name + ": Property is set, value is " + valArray[0]);
            }
        }
        else
        {
            valArray[0] = getSafeEmptyValue();
            if (log.isDebugEnabled())
            {
                log.debug(name + ": Property is not set, using emptyValue " + valArray[0]);
            }
        }

        try
        {
            /*
             * In the case we map a Group to an Object using mapToObject, and we
             * want to add an additional Field which should not be mapped, and
             * we leave the mapToProperty empty, we will get a NPE here. So we
             * have to double check, if we really have a setter set.
             */
            if(setter != null)
            {
                setter.invoke(obj, valArray);
            }
            else if (log.isDebugEnabled())
            {
                log.debug(name + ": has a null setter for the mapToProperty"
                        + " Attribute, although all Fields should be mapped"
                        + " to " + mapToObject + ". If this is unwanted, you"
                        + " should double check the mapToProperty Attribute, and"
                        + " consult the logs. The Turbine Intake Service will"
                        + " have logged a detailed Message with the error.");
            }
        }
        catch (IllegalAccessException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (InvocationTargetException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
    }

    /**
     * Used to throw an IntakeException when an error occurs executing the
     * get/set method of the mapped persistent object.
     *
     * @param type Type of method. (setter/getter)
     * @param fieldName Name of the field
     * @param groupName Name of the group
     * @param e Exception that was thrown
     * @throws IntakeException New exception with formatted message
     */
    private void throwSetGetException(String type, Object obj,
                                      String fieldName, String groupName,
                                      Exception e)
            throws IntakeException
    {
        throw new IntakeException("Could not execute " + type
                + " method for " + fieldName + " in group " + groupName
                + " on " + obj.getClass().getName(), e);

    }

    /**
     * Get the default Value
     *
     * @return the default value
     */
    public T getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Get the Value to use if the field is empty
     *
     * @return the value to use if the field is empty.
     */
    public T getEmptyValue()
    {
        return emptyValue;
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
    protected Object getSafeEmptyValue()
    {
        return getEmptyValue();
    }

    /**
     * Gets the name of the field.
     *
     * @return name of the field as specified in the XML file.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the display size of the field.  This is useful when
     * building the HTML input tag. If no displaySize was set,
     * an empty string is returned.
     *
     * @return the size information for this field
     */
    public String getDisplaySize()
    {
        return (StringUtils.isEmpty(displaySize) ? "" : displaySize);
    }

    /**
     * Gets the maximum size of the field.  This is useful when
     * building the HTML input tag.  The maxSize is set with the maxLength
     * rule. If this rule was not set, an empty string is returned.
     *
     * @return the maximum size information of the field
     */
    public String getMaxSize()
    {
        return (StringUtils.isEmpty(maxSize) ? "" : maxSize);
    }

    /**
     * Gets the String representation of the Value. This is basically a wrapper
     * method for the toString method which doesn't seem to show anything on
     * screen if accessed from Template. Name is also more in line with getValue
     * method which returns the actual Object.
     * This is useful for displaying correctly formatted data such as dates,
     * such as 18/11/1968 instead of the toString dump of a Date Object.
     *
     * @return the String Value
     */
    public String getStringValue()
    {
        return this.toString();
    }

    /**
     * Create a validator instance for the given class name
     *
     * @param validatorClassName the class name
     * @param field the related xml field containing the rule map
     * @return the validator instance
     * @throws IntakeException if the instance could not be created
     */
    @SuppressWarnings("unchecked")
    private Validator<T> createValidator(String validatorClassName)
            throws IntakeException
    {
        Validator<T> v;

        try
        {
            v = (Validator<T>)
                    Class.forName(validatorClassName).newInstance();
        }
        catch (InstantiationException e)
        {
            throw new IntakeException(
                    "Could not create new instance of Validator("
                    + validatorClassName + ")", e);
        }
        catch (IllegalAccessException e)
        {
            throw new IntakeException(
                    "Could not create new instance of Validator("
                    + validatorClassName + ")", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new IntakeException(
                    "Could not load Validator class("
                    + validatorClassName + ")", e);
        }

        if (v instanceof LogEnabled)
        {
        	((LogEnabled)v).enableLogging(log);
        }

        // this should always be true for now
        // (until bean property initialization is implemented)
        if (v instanceof InitableByConstraintMap)
        {
            ((InitableByConstraintMap) v).init(this.ruleMap);
        }
        else
        {
            throw new IntakeError(
                    "All Validation objects must be subclasses of "
                    + "InitableByConstraintMap");
        }

        return v;
    }
}
