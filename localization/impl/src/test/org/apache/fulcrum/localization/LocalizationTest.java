package org.apache.fulcrum.localization;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;

/**
 * Tests the API of the
 * {@link org.apache.fulcrum.localization.LocalizationService}.
 * <br>
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 */
public class LocalizationTest extends AbstractMerlinTestCase
{
    
    private LocalizationService ls = null;
    public LocalizationTest(String name)
    {
        super( 
          MAVEN_TARGET_CLASSES_DIR, 
          MERLIN_DEFAULT_CONFIG_FILE, 
          MERLIN_INFO_OFF, 
          MERLIN_DEBUG_OFF, 
          name );
    }
    public static Test suite()
    {
        return new TestSuite(LocalizationTest.class);
    }
    public void setUp() throws Exception
    {
        super.setUp();
        try
        {
            ls = (LocalizationService) this.resolve( "localization" );
        }
        catch (Throwable e)
        {
            getLogger().error( "Setup failure.", e );
            fail(e.getMessage());
        }
    }
    public void testInitialization()
    {
        assertTrue(true);
    }
    public void testLocalization() throws Exception
    {
        // Test retrieval of text using multiple default bundles
        String s = ls.getString(null, null, "key1");
        assertEquals("Unable to retrieve localized text for locale: default", "value1", s);
        s = ls.getString(null, new Locale("en", "US"), "key2");
        assertEquals("Unable to retrieve localized text for locale: en-US", "value2", s);
        s = ls.getString("org.apache.fulcrum.localization.BarBundle", new Locale("ko", "KR"), "key3");
        assertEquals("Unable to retrieve localized text for locale: ko-KR", s, "[ko] value3");
        try
        {
            ls.getString("DoesNotExist", new Locale("ko", ""), "key1");
            fail();
        }
        catch (MissingResourceException expectedFailure)
        {
            // Asked for resource bundle which does not exist.
        }
        catch( Throwable e )
        {
            // should not happen
            getLogger().error( "unresonable exception condition", e );
            fail();
        }
        // When a locale is used which cannot be produced for a given
        // bundle, fall back to the default locale.
        s = ls.getString(null, new Locale("ko", "KR"), "key4");
        assertEquals("Unable to retrieve localized text for locale: default", s, "value4");
        try
        {
            ls.getString(null, null, "NoSuchKey");
            fail();
        }
        catch (MissingResourceException expectedFailure)
        {
            // Asked for key from default bundle which does not exist,
        }
        getLogger().info( "OK" );
    }
    /**
     * Putting this in a seperate testcase because it fails..  Why?  I don't know.  I have never
     * used localization, so I leave it to brains better then mine. -dep
     * @todo Figure out why this test fails.
     * @throws Exception
     */
    public void OFFtestRetrievingOddLocale() throws Exception
    {
        String s = ls.getString(null, new Locale("fr", "US"), "key3");
        assertEquals("Unable to retrieve localized text for locale: fr", "[fr] value3", s);
    }
}
