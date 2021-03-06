package org.apache.fulcrum.intake.validator;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <caption>Validation rules</caption>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>format</td><td>see SimpleDateFormat javadoc</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>formatx</td><td>see SimpleDateFormat javadoc</td>
 * <td>&nbsp;</td></tr>
 * <tr><td colspan=3>where x is &gt;= 1 to specify multiple date
 *         formats.  Only one format rule should have a message</td></tr>
 * <tr><td>flexible</td><td>true, as long as DateFormat can parse the date,
 *                            allow it, and false</td>
 * <td>false</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class DateStringValidator
        extends DefaultValidator<Date>
{
    private static final String DEFAULT_DATE_MESSAGE =
            "Date could not be parsed";

    /** A list of date formats to try */
    private List<String> dateFormats = null;

    /** An error message if no date could be parsed */
    private String dateFormatMessage = null;

    /** A flag that is passed to the DateFormat lenient feature */
    private boolean flexible = false;

    /**
     * Default Constructor
     */
    public DateStringValidator()
    {
        super();
        dateFormats = new ArrayList<String>(5);
    }

    /**
     * Constructor to use when initializing Object
     *
     * @param paramMap a map of parameters
     * @throws InvalidMaskException one of the mask rules is invalid
     */
    @Override
	public void init(Map<String, ? extends Constraint> paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint = paramMap.get(FORMAT_RULE_NAME);

        if (constraint != null)
        {
            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
        }

        for(int i = 1 ;; i++)
        {
            constraint = paramMap.get(FORMAT_RULE_NAME + i);

            if (constraint == null)
            {
                break; // for
            }

            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
        }

        if (StringUtils.isEmpty(dateFormatMessage))
        {
            dateFormatMessage = DEFAULT_DATE_MESSAGE;
        }

        constraint = paramMap.get(FLEXIBLE_RULE_NAME);

        if (constraint != null)
        {
            flexible = Boolean.valueOf(constraint.getValue()).booleanValue();
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @throws ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    @Override
	public void assertValidity(String testValue)
            throws ValidationException
    {
        super.assertValidity(testValue);

        if (required || StringUtils.isNotEmpty(testValue))
        {
            try
            {
                parse(testValue);
            }
            catch (ParseException e)
            {
                errorMessage = dateFormatMessage;
                throw new ValidationException(dateFormatMessage);
            }
        }
    }

    /**
     * Parses the String s according to the rules/formats for this validator.
     * The formats provided by the "formatx" rules (where x is &gt;= 1) are
     * used <strong>before</strong> the "format" rules to allow for a display
     * format that includes a 4 digit year, but that will parse the date using
     * a format that accepts 2 digit years.
     *
     * @param s possibly a date string
     * @return the date parsed
     * @throws ParseException indicates that the string could not be
     * parsed into a date.
     */
    public Date parse(String s)
            throws ParseException
    {
        Date date = null;

        if (s == null)
        {
            throw new ParseException("Input string was null", -1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setLenient(flexible);

        for (int i = 1; i < dateFormats.size() && date == null; i++)
        {
            sdf.applyPattern(dateFormats.get(i));

            try
            {
                date = sdf.parse(s);
            }
            catch (ParseException e)
            {
                // ignore
            }
        }

        if (date == null)
        {
            sdf.applyPattern(dateFormats.get(0));

            try
            {
                date = sdf.parse(s);
            }
            catch (ParseException e)
            {
                // ignore
            }
        }

        if (date == null)
        {
            // Try default
            date = SimpleDateFormat.getInstance().parse(s);
        }

        // if the date still has not been parsed at this point, throw
        // a ParseException.
        if (date == null)
        {
            throw new ParseException("Could not parse the date", 0);
        }

        return date;
    }

    /**
     * Formats a date into a String.  The format used is from
     * the first format rule found for the field.
     *
     * @param date the Date object to convert into a string.
     * @return formatted date
     */
    public String format(Date date)
    {
        String s = null;

        if (date != null && !dateFormats.isEmpty())
        {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormats.get(0));
            s = sdf.format(date);
        }

        return s;
    }


    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minLengthMessage.
     *
     * @return value of minLengthMessage.
     */
    public String getDateFormatMessage()
    {
        return dateFormatMessage;
    }

    /**
     * Only sets the message if the new message has some information.
     * So the last setMessage call with valid data wins.  But later calls
     * with null or empty string will not affect a previous valid setting.
     *
     * @param message  Value to assign to minLengthMessage.
     */
    public void setDateFormatMessage(String message)
    {
        if (StringUtils.isNotEmpty(message))
        {
            dateFormatMessage = message;
        }
    }

    /**
     * Get the value of dateFormats.
     *
     * @return value of dateFormats.
     */
    public List<String> getDateFormats()
    {
        return dateFormats;
    }

    /**
     * Set the value of dateFormats.
     *
     * @param formats  Value to assign to dateFormats.
     */
    public void setDateFormats(List<String> formats)
    {
        this.dateFormats = formats;
    }

    /**
     * Get the value of flexible.
     *
     * @return value of flexible.
     */
    public boolean isFlexible()
    {
        return flexible;
    }

    /**
     * Set the value of flexible.
     *
     * @param flexible  Value to assign to flexible.
     */
    public void setFlexible(boolean flexible)
    {
        this.flexible = flexible;
    }
}
