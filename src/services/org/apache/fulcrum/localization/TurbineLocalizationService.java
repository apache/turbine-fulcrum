package org.apache.fulcrum.localization;

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

import java.text.MessageFormat;
import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Strings;
import org.apache.log4j.Category;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;

/**
 * <p>This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.</p>
 *
 * <p>Usage example:</p>
 * 
 * <blockquote><code><pre>
 * LocalizationService ls = (LocalizationService) TurbineServices
 *     .getInstance().getService(LocalizationService.SERVICE_NAME);
 * </pre></code></blockquote>
 *
 * <p>Then call {@link #getString(String, Locale, String)}, or one of
 * four methods to retrieve a ResourceBundle:
 *
 * <ul>
 * <li>getBundle("MyBundleName")</li>
 * <li>getBundle("MyBundleName", httpAcceptLanguageHeader)</li>
 * <li>etBundle("MyBundleName", HttpServletRequest)</li>
 * <li>getBundle("MyBundleName", Locale)</li>
 * <li>etc.</li>
 * </ul></p>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:novalidemail@foo.com">Frank Y. Kim</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@collab.net">Leonard Richardson</a>
 * @version $Id$
 */
public class TurbineLocalizationService
    extends BaseService
    implements LocalizationService
{
    /**
     * The value to pass to <code>MessageFormat</code> if a
     * <code>null</code> reference is passed to <code>format()</code>.
     */
    private static final Object[] NO_ARGS = new Object[0];

    /**
     * Bundle name keys a HashMap of the ResourceBundles in this
     * service (which is in turn keyed by Locale).
     */
    private HashMap bundles = null;

    /**
     * The list of default bundles to search.
     */
    private String[] bundleNames = null;

    /**
     * The name of the default locale to use (includes language and
     * country).
     */
    private Locale defaultLocale = null;

    /** The name of the default language to use. */
    private String defaultLanguage;

    /** The name of the default country to use. */
    private String defaultCountry = null;

    /**
     * Log4J logging category.
     */
    private Category category = Category.getInstance(getClass().getName());

    /**
     * Creates a new instance.
     */
    public TurbineLocalizationService()
    {
        bundles = new HashMap();
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        initBundleNames(null);

        Locale jvmDefault = Locale.getDefault();
        defaultLanguage = getConfiguration()
            .getString("locale.default.language",
                       jvmDefault.getLanguage()).trim();
        defaultCountry = getConfiguration()
            .getString("locale.default.country",
                       jvmDefault.getCountry()).trim();
        defaultLocale = new Locale(defaultLanguage, defaultCountry);

        setInit(true);
    }

    /**
     * Initialize list of default bundle names.
     *
     * @param names Ignored.
     */
    protected void initBundleNames(String[] ignored)
    {
        //System.err.println("cfg=" + getConfiguration());
        bundleNames =
            getConfiguration().getStringArray("locale.default.bundles");
        String name = getConfiguration().getString("locale.default.bundle");
        if (name != null && name.length() > 0)
        {
            // Using old-style single bundle name property.
            if (bundleNames == null || bundleNames.length <= 0)
            {
                bundleNames = new String[] { name };
            }
            else
            {
                // Prepend "default" bundle name.
                String[] array = new String[bundleNames.length + 1];
                array[0] = name;
                System.arraycopy(bundleNames, 0, array, 1, bundleNames.length);
                bundleNames = array;
            }
        }
        if (bundleNames == null)
        {
            bundleNames = new String[0];
        }
    }

    /**
     * Retrieves the default language (specified in the config file).
     */
    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    /**
     * Retrieves the default country (specified in the config file).
     */
    public String getDefaultCountry()
    {
        return defaultCountry;
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getDefaultBundleName()
     */
    public String getDefaultBundleName()
    {
        return (bundleNames.length > 0 ? bundleNames[0] : "");
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getBundleNames()
     */
    public String[] getBundleNames()
    {
        return (String []) bundleNames.clone();
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getBundle()
     */
    public ResourceBundle getBundle()
    {
        return getBundle(getDefaultBundleName(), (Locale) null);
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getBundle(String)
     */
    public ResourceBundle getBundle(String bundleName)
    {
        return getBundle(bundleName, (Locale) null);
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, String languageHeader)
    {
        return getBundle(bundleName, getLocale(languageHeader));
    }

    /**
     * This method returns a ResourceBundle given the Locale
     * information supplied in the HTTP "Accept-Language" header which
     * is stored in HttpServletRequest.
     *
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(HttpServletRequest req)
    {
        return getBundle(getDefaultBundleName(), getLocale(req));
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header which is stored in HttpServletRequest.
     *
     * @param bundleName Name of the bundle to use if the request's
     * locale cannot be resolved.
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, HttpServletRequest req)
    {
        return getBundle(bundleName, getLocale(req));
    }

    /**
     * This method returns a ResourceBundle for the given bundle name
     * and the given Locale.
     *
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, Locale locale)
    {
        // Assure usable inputs.
        bundleName = (bundleName == null ? getDefaultBundleName() :
                      bundleName.trim());
        if (locale == null)
        {
            locale = getLocale((String) null);
        }

        // Find/retrieve/cache bundle.
        ResourceBundle rb = null;
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);
        if (bundlesByLocale != null)
        {
            // Cache of bundles by locale for the named bundle exists.
            // Check the cache for a bundle corresponding to locale.
            rb = (ResourceBundle) bundlesByLocale.get(locale);

            if (rb == null)
            {
                // Not yet cached.
                rb = cacheBundle(bundleName, locale);
            }
        }
        else
        {
            rb = cacheBundle(bundleName, locale);
        }
        return rb;
    }

    /**
     * Caches the named bundle for fast lookups.  This operation is
     * relatively expesive in terms of memory use, but is optimized
     * for run-time speed in the usual case.
     *
     * @exception MissingResourceException Bundle not found.
     */
    private synchronized ResourceBundle cacheBundle(String bundleName,
                                                    Locale locale)
        throws MissingResourceException
    {
        HashMap bundlesByLocale = (HashMap) bundles.get(bundleName);
        ResourceBundle rb = (bundlesByLocale == null ? null :
                             (ResourceBundle) bundlesByLocale.get(locale));

        if (rb == null)
        {
            bundlesByLocale = (bundlesByLocale == null ? new HashMap(3) :
                               new HashMap(bundlesByLocale));
            try
            {
                rb = ResourceBundle.getBundle(bundleName, locale);
            }
            catch (MissingResourceException e)
            {
                rb = findBundleByLocale(bundleName, locale, bundlesByLocale);
                if (rb == null)
                {
                    throw (MissingResourceException) e.fillInStackTrace();
                }
            }

            if (rb != null)
            {
                // Cache bundle.
                bundlesByLocale.put(rb.getLocale(), rb);

                HashMap bundlesByName = new HashMap(bundles);
                bundlesByName.put(bundleName, bundlesByLocale);
                this.bundles = bundlesByName;
            }
        }
        return rb;
    }

    /**
     * <p>Retrieves the bundle most closely matching first against the
     * supplied inputs, then against the defaults.</p>
     *
     * <p>Use case: some clients send a HTTP Accept-Language header
     * with a value of only the language to use
     * (i.e. "Accept-Language: en"), and neglect to include a country.
     * When there is no bundle for the requested language, this method
     * can be called to try the default country (checking internally
     * to assure the requested criteria matches the default to avoid
     * disconnects between language and country).</p>
     *
     * <p>Since we're really just guessing at possible bundles to use,
     * we don't ever throw <code>MissingResourceException</code>.</p>
     */
    private ResourceBundle findBundleByLocale(String bundleName, Locale locale,
                                              Map bundlesByLocale)
    {
        ResourceBundle rb = null;
        if ( !Strings.isValid(locale.getCountry()) &&
             defaultLanguage.equals(locale.getLanguage()) )
        {
            /*
            category.debug("Requested language '" + locale.getLanguage() +
                           "' matches default: Attempting to guess bundle " +
                           "using default country '" + defaultCountry + '\'');
            */
            Locale withDefaultCountry = new Locale(locale.getLanguage(),
                                                   defaultCountry);
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultCountry);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultCountry);
            }
        }
        else if ( !Strings.isValid(locale.getLanguage()) &&
                  defaultCountry.equals(locale.getCountry()) )
        {
            Locale withDefaultLanguage = new Locale(defaultLanguage, 
                                                    locale.getCountry());
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultLanguage);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultLanguage);
            }
        }

        if (rb == null && !defaultLocale.equals(locale))
        {
            rb = getBundleIgnoreException(bundleName, defaultLocale);
        }

        return rb;
    }

    /**
     * Retrieves the bundle using the
     * <code>ResourceBundle.getBundle(String, Locale)</code> method,
     * returning <code>null</code> instead of throwing
     * <code>MissingResourceException</code>.
     */
    private final ResourceBundle getBundleIgnoreException(String bundleName,
                                                          Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle(bundleName, locale);
        }
        catch (MissingResourceException ignored)
        {
            return null;
        }
    }

    /**
     * This method sets the name of the first bundle in the search
     * list (the "default" bundle).
     *
     * @param defaultBundle Name of default bundle.
     */
    public void setBundle(String defaultBundle)
    {
        if (bundleNames.length > 0)
        {
            bundleNames[0] = defaultBundle;
        }
        else
        {
            synchronized (this)
            {
                if (bundleNames.length <= 0)
                {
                    bundleNames = new String[] { defaultBundle };
                }
            }
        }
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getLocale(HttpServletRequest)
     */
    public Locale getLocale(HttpServletRequest req)
    {
        return getLocale(req.getHeader(ACCEPT_LANGUAGE));
// (JSS) Backed out this change because Tomcat seems to be returning 
//       the wrong result and things just are not working.
//        Locale l = req.getLocale();
//        return (l != null ? l : getLocale(req.getHeader(ACCEPT_LANGUAGE)));
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#getLocale(String)
     */
    public Locale getLocale(String header)
    {
        if (!Strings.isEmpty(header))
        {
            LocaleTokenizer tok = new LocaleTokenizer(header);
            if (tok.hasNext())
            {
                return (Locale) tok.next();
            }
        }

        // Couldn't parse locale.
        return defaultLocale;
    }

    /**
     * @exception MissingResourceException Specified key cannot be matched.
     * @see org.apache.fulcrum.localization.LocalizationService#getString(String, Locale, String)
     */
    public String getString(String bundleName, Locale locale, String key)
    {
        String value = null;

        if (locale == null)
        {
            locale = getLocale((String) null);
        }

        // Look for text in requested bundle.
        ResourceBundle rb = getBundle(bundleName, locale);
        value = getStringOrNull(rb, key);

        // Look for text in list of default bundles.
        if (value == null && bundleNames.length > 0)
        {
            String name;
            for (int i = 0; i < bundleNames.length; i++)
            {
                name = bundleNames[i];
                //System.out.println("getString(): name=" + name +
                //                   ", locale=" + locale + ", i=" + i);
                if (!name.equals(bundleName))
                {
                    rb = getBundle(name, locale);
                    value = getStringOrNull(rb, key);
                    if (value != null)
                    {
                        locale = rb.getLocale();
                        break;
                    }
                }
            }
        }

        if (value == null)
        {
            String loc = locale.toString();
            category.debug(LocalizationService.SERVICE_NAME +
                           " noticed missing resource: " +
                           "bundleName=" + bundleName + ", locale=" + loc +
                           ", key=" + key);
            // Text not found in requested or default bundles.
            throw new MissingResourceException(bundleName, loc, key);
        }

        return value;
    }

    /**
     * Gets localized text from a bundle if it's there.  Otherwise,
     * returns <code>null</code> (ignoring a possible
     * <code>MissingResourceException</code>).
     */
    protected final String getStringOrNull(ResourceBundle rb, String key)
    {
        if (rb != null)
        {
            try
            {
                return rb.getString(key);
            }
            catch (MissingResourceException ignored)
            {
            }
        }
        return null;
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#format(String, Locale, String, Object)
     */
    public String format(String bundleName, Locale locale, 
                         String key, Object arg1)
    {
        return format(bundleName, locale, key, new Object[] { arg1 });
    }

    /**
     * @see org.apache.fulcrum.localization.LocalizationService#format(String, Locale, String, Object, Object)
     */
    public String format(String bundleName, Locale locale,
                         String key, Object arg1, Object arg2)
    {
        return format(bundleName, locale, key, new Object[] { arg1, arg2 });
    }

    /**
     * Looks up the value for <code>key</code> in the
     * <code>ResourceBundle</code> referenced by
     * <code>bundleName</code>, then formats that value for the
     * specified <code>Locale</code> using <code>args</code>.
     *
     * @return Localized, formatted text identified by
     * <code>key</code>.
     */
    public String format(String bundleName, Locale locale,
                         String key, Object[] args)
    {
        if (locale == null)
        {
            // When formatting Date objects and such, MessageFormat
            // cannot have a null Locale.
            locale = getLocale((String) null);
        }

        String value = getString(bundleName, locale, key);
        if (args == null)
        {
            args = NO_ARGS;
        }

        // FIXME: after switching to JDK 1.4, it will be possible to clean
        // this up by providing the Locale along with the string in the
        // constructor to MessageFormat.  Until 1.4, the following workaround
        // is required for constructing the format with the appropriate locale:
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.setLocale(locale);
        messageFormat.applyPattern(value);

        StringBuffer buf = new StringBuffer();
        messageFormat.format(args, buf, new FieldPosition(0));
        return buf.toString();
    }
}
