package org.apache.fulcrum.mimetype;
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
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.fulcrum.mimetype.util.CharSetMap;
import org.apache.fulcrum.mimetype.util.MimeType;
import org.apache.fulcrum.mimetype.util.MimeTypeMap;
/**
 * The MimeType Service maintains mappings between MIME types and
 * the corresponding file name extensions, and between locales and
 * character encodings.
 *
 * <p>The MIME type mappings can be defined in MIME type files
 * located in user's home directory, Java home directory or
 * the current class jar. The default mapping file is defined
 * with the mime.type.file property. In addition, the service maintains
 * a set of most common mappings.
 *
 * <p>The charset mappings can be defined in property files
 * located in user's home directory, Java home directory or
 * the current class jar. The default mapping file is defined
 * with the charset.file property. In addition, the service maintains
 * a set of most common mappings.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Id$
 *
 * @avalon.component name="mimetype" lifestyle="singleton"
 * @avalon.service type="org.apache.fulcrum.mimetype.MimeTypeService"
 */
public class DefaultMimeTypeService
    extends AbstractLogEnabled
    implements MimeTypeService, Configurable, Initializable, Contextualizable
{
    private String applicationRoot;
    /**
     * The MIME type file property.
     */
    public static final String MIME_TYPES = "mimetypes";
    /**
     * The charset file property.
     */
    public static final String CHARSETS = "charsets";
    // path to a mimetypes-file_extension mapping file
    private String mimetypePath;
    // path to a charset-language mapping file
    private String charsetPath;
    /**
     * The MIME type map used by the service.
     */
    private MimeTypeMap mimeTypeMap;
    /**
     * The charset map used by the service.
     */
    private CharSetMap charSetMap;
    /** The Avalon Context */
    private Context context = null;
    /**
     * Constructs a new service.
     */
    public DefaultMimeTypeService()
    {
    }
    /**
     * Sets a MIME content type mapping to extensions to the map.
     * The extension is specified by a MIME type name followed
     * by a list of file name extensions separated by a whitespace.
     *
     * @param spec a MIME type extension specification to add.
     */
    public void setContentType(String spec)
    {
        mimeTypeMap.setContentType(spec);
    }
    /**
     * Gets the MIME content type for a file as a string.
     *
     * @param file The file to look up a MIME type mapping for.
     * @return the MIME type string.
     */
    public String getContentType(File file)
    {
        return mimeTypeMap.getContentType(file);
    }
    /**
     * Gets the MIME content type for a named file as a string.
     *
     * @param fileName The name of the file to look up a MIME type
     * mapping for.
     * @return the MIME type string.
     */
    public String getContentType(String fileName)
    {
        return mimeTypeMap.getContentType(fileName);
    }
    /**
     * Gets the MIME content type for a file name extension as a string.
     *
     * @param fileName The name of the file to look up a MIME type
     * mapping for.
     * @param def The default MIME type to use if no mapping exists.
     * @return the MIME type string.
     */
    public String getContentType(String fileName, String def)
    {
        return mimeTypeMap.getContentType(fileName, def);
    }
    /**
     * Gets the MIME content type for a file.
     *
     * @param file the file.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(File file)
    {
        return mimeTypeMap.getMimeContentType(file);
    }
    /**
     * Gets the MIME content type for a named file.
     *
     * @param name the name of the file.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(String name)
    {
        return mimeTypeMap.getMimeContentType(name);
    }
    /**
     * Gets the MIME content type for a file name extension.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(String ext, String def)
    {
        return mimeTypeMap.getMimeContentType(ext, def);
    }
    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param mime the MIME type as a string.
     * @return the file name extension or null.
     */
    public String getDefaultExtension(String type)
    {
        return mimeTypeMap.getDefaultExtension(type);
    }
    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param mime the MIME type.
     * @return the file name extension or null.
     */
    public String getDefaultExtension(MimeType mime)
    {
        return mimeTypeMap.getDefaultExtension(mime);
    }
    /**
     * Sets a locale-charset mapping.
     *
     * @param key the key for the charset.
     * @param charset the corresponding charset.
     */
    public void setCharSet(String key, String charset)
    {
        charSetMap.setCharSet(key, charset);
    }
    /**
     * Gets the charset for a locale. First a locale specific charset
     * is searched for, then a country specific one and lastly a language
     * specific one. If none is found, the default charset is returned.
     *
     * @param locale the locale.
     * @return the charset.
     */
    public String getCharSet(Locale locale)
    {
        return charSetMap.getCharSet(locale);
    }
    /**
     * Gets the charset for a locale with a variant. The search
     * is performed in the following order:
     * "lang"_"country"_"variant"="charset",
     * _"counry"_"variant"="charset",
     * "lang"__"variant"="charset",
     * __"variant"="charset",
     * "lang"_"country"="charset",
     * _"country"="charset",
     * "lang"="charset".
     * If nothing of the above is found, the default charset is returned.
     *
     * @param locale the locale.
     * @param variant a variant field.
     * @return the charset.
     */
    public String getCharSet(Locale locale, String variant)
    {
        return charSetMap.getCharSet(locale, variant);
    }
    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @return the found charset or the default one.
     */
    public String getCharSet(String key)
    {
        return charSetMap.getCharSet(key);
    }
    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @param def the default charset if none is found.
     * @return the found charset or the given default.
     */
    public String getCharSet(String key, String def)
    {
        return charSetMap.getCharSet(key, def);
    }

    private String getRealPath(String path)
    {
        String absolutePath = null;
        if (applicationRoot == null)
        {
            absolutePath = new File(path).getAbsolutePath();
        }
        else
        {
            absolutePath = new File(applicationRoot, path).getAbsolutePath();
        }
        return absolutePath;
    }
    // ---------------- Avalon Lifecycle Methods ---------------------
    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf) 
    {
        mimetypePath = conf.getAttribute(MIME_TYPES, null);
        charsetPath = conf.getAttribute(CHARSETS, null);
        if (mimetypePath != null)
        {
            mimetypePath = getRealPath(mimetypePath);
        }
        if (charsetPath != null)
        {
            charsetPath = getRealPath(charsetPath);
        }
    }
    /**
     * Avalon component lifecycle method
     */
    public void initialize() throws Exception
    {
        if (mimetypePath != null)
        {
            try
            {
                mimeTypeMap = new MimeTypeMap(mimetypePath);
            }
            catch (IOException x)
            {
                throw new Exception(mimetypePath, x);
            }
        }
        else
        {
            mimeTypeMap = new MimeTypeMap();
        }
        if (charsetPath != null)
        {
            try
            {
                charSetMap = new CharSetMap(charsetPath);
            }
            catch (IOException x)
            {
                throw new Exception(charsetPath, x);
            }
        }
        else
        {
            charSetMap = new CharSetMap();
        }
    }
    
    /**
     * @see org.apache.avalon.framework.context.Contextualizable
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */    
    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
        this.applicationRoot = context.get( "urn:avalon:home" ).toString();
    }
    
}
