package org.apache.fulcrum.parser;

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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * ValueParser is a base interface for classes that need to parse
 * name/value Parameters, for example GET/POST data or Cookies
 * (ParameterParser and CookieParser)
 *
 * <p>NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jh@byteaction.de">J&#252;rgen Hoffmann</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public interface ValueParser extends Iterable<String>
{
    /**
     * The default character encoding to use when converting to byte arrays
     */
    String DEFAULT_CHARACTER_ENCODING = "US-ASCII";

    /** Possible values for the URL folding setting */
    enum URLCaseFolding {
        /** No folding set */
        UNSET,

        /** No folding */
        NONE,

        /** Fold to lower case */
        LOWER,

        /** Fold to upper case */
        UPPER
    }

    /**
     * Clear all name/value pairs out of this object.
     */
    void clear();
    
    /**
     * Dispose all references of this object. 
     * 
     * Instead of  org.apache.fulcrum.pool.Recyclable interface we use this, 
     * may change this again..
     */
    void dispose();

    /**
     * Set the character encoding that will be used by this ValueParser.
     * 
     * @param characterEncoding the character encoding to use
     */
    void setCharacterEncoding(String characterEncoding);

    /**
     * Get the character encoding that will be used by this ValueParser.
     * 
     * @return Current character encoding
     */
    String getCharacterEncoding();

    /**
     * Set the locale that will be used by this ValueParser.
     * 
     * @param locale the default locale to be used by the parser 
     */
    void setLocale(Locale locale);

    /**
     * Get the locale that will be used by this ValueParser.
     * 
     * @return Locale the locale being used
     */
    Locale getLocale();

    /**
     * Set the date format that will be used by this ValueParser.
     * 
     * @param dateFormat the date format
     */
    void setDateFormat(DateFormat dateFormat);

    /**
     * Get the date format that will be used by this ValueParser.
     * 
     * @return DateFormat current date format used by this ValueParser
     */
    DateFormat getDateFormat();

    /**
     * Set the number format that will be used by this ValueParser.
     * 
     * @param numberFormat the number format to use
     */
    void setNumberFormat(NumberFormat numberFormat);

    /**
     * Get the number format that will be used by this ValueParser.
     * 
     * @return NumberFormat the current number format
     */
    NumberFormat getNumberFormat();

    /**
     * Trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING. It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    String convert(String value);

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A double with the value.
     */
    void add(String name, double value);

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An int with the value.
     */
    void add(String name, int value);

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An Integer with the value.
     */
    void add(String name, Integer value);

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    void add(String name, long value);

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    void add(String name, String value);

    /**
     * Add an array of Strings for a key. This
     * is simply adding all the elements in the
     * array one by one.
     *
     * @param name A String with the name.
     * @param value A String Array.
     */
    void add(String name, String [] value);

    /**
     * Removes the named parameter from the contained hashtable. Wraps to the
     * contained <code>Hashtable.remove()</code>.
     *
     *
     * @param name the name of the mapped value to remove
     * @return The value that was mapped to the key (a <code>String[]</code>)
     *         or <code>null</code> if the key was not mapped.
     */
    Object remove(String name);

    /**
     * Determine whether a given key has been inserted.  All keys are
     * stored in lowercase strings, so override method to account for
     * this.
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    boolean containsKey(Object key);

    /**
     * Gets the keys.
     *
     * @return A <code>Set</code> of the keys.
     */
    Set<String> keySet();

    /**
     * Returns all the available parameter names.
     *
     * @return A object array with the keys.
     */
    String[] getKeys();

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A boolean.
     */
    boolean getBoolean(String name, boolean defaultValue);

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A boolean.
     */
    boolean getBoolean(String name);

    /**
     * Returns a Boolean object for the given name.  If the parameter
     * does not exist or can not be parsed as a boolean, null is returned.
     * <p>
     * Valid values for true: true, on, 1, yes<br>
     * Valid values for false: false, off, 0, no<br>
     * <p>
     * The string is compared without reguard to case.
     *
     * @param name A String with the name.
     * @return A Boolean.
     */
    Boolean getBooleanObject(String name);

    /**
     * Returns a Boolean object for the given name.  If the parameter
     * does not exist or can not be parsed as a boolean, the defaultValue
     * is returned.
     * <p>
     * Valid values for true: true, on, 1, yes<br>
     * Valid values for false: false, off, 0, no<br>
     * <p>
     * The string is compared without regard to case.
     *
     * @param name A String with the name.
     * @param defaultValue boolean default if not found
     * @return A Boolean.
     */
    Boolean getBooleanObject(String name, Boolean defaultValue);

    /**
     * Return an array of booleans for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A boolean[].
     */
    boolean[] getBooleans(String name);

    /**
     * Return an array of Booleans for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A Boolean[].
     */
    Boolean[] getBooleanObjects(String name);

    /**
     * Return a double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    double getDouble(String name, double defaultValue);

    /**
     * Return a double for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A double.
     */
    double getDouble(String name);

    /**
     * Return an array of doubles for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A double[].
     */
    double[] getDoubles(String name);

    /**
     * Return a Double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    Double getDoubleObject(String name, Double defaultValue);

    /**
     * Return a Double for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A double.
     */
    Double getDoubleObject(String name);

    /**
     * Return an array of doubles for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A double[].
     */
    Double[] getDoubleObjects(String name);

    /**
     * Return a float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A float.
     */
    float getFloat(String name, float defaultValue);

    /**
     * Return a float for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A float.
     */
    float getFloat(String name);

    /**
     * Return an array of floats for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A float[].
     */
    float[] getFloats(String name);

    /**
     * Return a Float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Float.
     */
    Float getFloatObject(String name, Float defaultValue);

    /**
     * Return a float for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A Float.
     */
    Float getFloatObject(String name);

    /**
     * Return an array of floats for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A float[].
     */
    Float[] getFloatObjects(String name);

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A BigDecimal.
     */
    BigDecimal getBigDecimal(String name, BigDecimal defaultValue);

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return <code>null</code>.
     *
     * @param name A String with the name.
     * @return A BigDecimal.
     */
    BigDecimal getBigDecimal(String name);

    /**
     * Return an array of BigDecimals for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A BigDecimal[].
     */
    BigDecimal[] getBigDecimals(String name);

    /**
     * Return an int for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An int.
     */
    int getInt(String name, int defaultValue);

    /**
     * Return an int for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return An int.
     */
    int getInt(String name);

    /**
     * Return an Integer for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An Integer.
     */
    Integer getIntObject(String name, Integer defaultValue);

    /**
     * Return an Integer for the given name.  If the name does not exist,
     * return null.
     *
     * @param name A String with the name.
     * @return An Integer.
     */
    Integer getIntObject(String name);

    /**
     * Return an array of ints for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return An int[].
     */
    int[] getInts(String name);

    /**
     * Return an array of Integers for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Integer[].
     */
    Integer[] getIntObjects(String name);

    /**
     * Return a long for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A long.
     */
    long getLong(String name, long defaultValue);

    /**
     * Return a long for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A long.
     */
    long getLong(String name);

    /**
     * Return a Long for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Long.
     */
    Long getLongObject(String name, Long defaultValue);

    /**
     * Return a Long for the given name.  If the name does not exist,
     * return null.
     *
     * @param name A String with the name.
     * @return A Long.
     */
    Long getLongObject(String name);

    /**
     * Return an array of longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A long[].
     */
    long[] getLongs(String name);

    /**
     * Return an array of Longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A Long[].
     */
    Long[] getLongObjects(String name);

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    byte getByte(String name, byte defaultValue);

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A byte.
     */
    byte getByte(String name);

    /**
     * Return an array of bytes for the given name.  If the name does
     * not exist, return null. The array is returned according to the
     * HttpRequest's character encoding.
     *
     * @param name A String with the name.
     * @return A byte[].
     * @throws UnsupportedEncodingException Generic exception
     */
    byte[] getBytes(String name) throws UnsupportedEncodingException;

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    Byte getByteObject(String name, Byte defaultValue);

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A byte.
     */
    Byte getByteObject(String name);

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A String.
     */
    String getString(String name);

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null. It is the same as the getString() method
     * however has been added for simplicity when working with
     * template tools such as Velocity which allow you to do
     * something like this:
     *
     * <code>$data.Parameters.form_variable_name</code>
     *
     * @param name A String with the name.
     * @return A String.
     */
    String get(String name);

    /**
     * Return a String for the given name.  If the name does not
     * exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String.
     */
    String getString(String name, String defaultValue);

    /**
     * Set a parameter to a specific value.
     *
     * This is useful if you want your action to override the values
     * of the parameters for the screen to use.
     * @param name The name of the parameter.
     * @param value The value to set.
     */
    void setString(String name, String value);

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A String[].
     */
    String[] getStrings(String name);

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String[].
     */
    String[] getStrings(String name, String[] defaultValue);

    /**
     * Set a parameter to a specific value.
     *
     * This is useful if you want your action to override the values
     * of the parameters for the screen to use.
     * @param name The name of the parameter.
     * @param values The value to set.
     */
    void setStrings(String name, String[] values);

    /**
     * Return an Object for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return An Object.
     */
    Object getObject(String name);

    /**
     * Return an array of Objects for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Object[].
     */
    Object[] getObjects(String name);

    /**
     * Returns a java.util.Date object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return the
     * defaultValue.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @param defaultValue The default value.
     * @return A Date.
     */
    Date getDate(String name, DateFormat df, Date defaultValue);

    /**
     * Returns a java.util.Date object.  If there are DateSelector
     * style parameters then these are used.  If not and there is a
     * parameter 'name' then this is parsed by DateFormat.  If the
     * name does not exist, return null.
     *
     * @param name A String with the name.
     * @return A Date.
     */
    Date getDate(String name);

    /**
     * Returns a java.util.Date object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return null.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @return A Date.
     */
    Date getDate(String name, DateFormat df);

    /**
     * Uses bean introspection to set writable properties of bean from
     * the parameters, where a (case-insensitive) name match between
     * the bean property and the parameter is looked for.
     *
     * @param bean An Object.
     * @exception Exception a generic exception.
     */
    void setProperties(Object bean) throws Exception;

    /**
     * Simple method that attempts to get a toString() representation
     * of this object.  It doesn't do well with String[]'s though.
     *
     * @return A String.
     */
    @Override
    String toString();

    /**
     * Convert a String value according to the url-case-folding property.
     *
     * @param value the String to convert
     *
     * @return a new String.
     *
     */
    String convertAndTrim(String value);

    /**
     * A static version of the convert method, which
     * trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @param folding the type of URL case folding to be used
     * @return A new String converted and trimmed.
     */
    String convertAndTrim(String value, URLCaseFolding folding);

    /**
     * Gets the folding value from the ParserService configuration
     *
     * @return The current Folding Value
     */
    URLCaseFolding getUrlFolding();
}

