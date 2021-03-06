package org.apache.fulcrum.template.velocity;


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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.fulcrum.template.BaseTemplateEngineService;
import org.apache.fulcrum.template.TemplateContext;
import org.apache.fulcrum.template.TemplateException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.exception.MethodInvocationException;

/**
 * This is a Service that can process Velocity templates from within a
 * Turbine Screen.  Here's an example of how you might use it from a
 * screen:<br>
 *
 * <code><pre>
 * Context context = new VelocityContext();
 * context.put("message", "Hello from Turbine!");
 * String results = VelocityServiceFacade.handleRequest(context,"HelloWorld.vm");
 * </pre></code>
 *
 * Character sets map codes to glyphs, while encodings map between
 * chars/bytes and codes.
 * <i>bytes -> [encoding] -> charset -> [rendering] -> glyphs</i>
 *
 * @author <a href="mailto:epugh@opensourceconnections.com">Eric Pugh</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.ent">Sean Legassick</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public class DefaultVelocityService
    extends BaseTemplateEngineService
    implements VelocityService, Initializable
{
    /**
     * The generic resource loader path property in velocity.
     */
    private static final String RESOURCE_LOADER_PATH =
        ".resource.loader.path";

    /**
     * Default character set to use if not specified by caller.
     */
    private static final String DEFAULT_CHAR_SET = "ISO-8859-1";

    /**
     * The prefix used for URIs which are of type <code>jar</code>.
     */
    private static final String JAR_PREFIX = "jar:";

    /**
     * The prefix used for URIs which are of type <code>absolute</code>.
     */
    private static final String ABSOLUTE_PREFIX = "file://";

    /**
     * The EventCartridge that is used against all contexts
     */
    private EventCartridge eventCartridge;

    /**
     * Whether or not to use the eventCartridge. Defaults to true.
     * Can be used to turn off EC processing.
     */
    private boolean eventCartridgeEnabled = true;


    // put conf into object to pass to the velocity engine
    org.apache.commons.configuration.Configuration velocityConf;

    /**
     * The VelocityEngine used by the service to merge templates
     */
    private VelocityEngine velocityEngine;

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(TemplateContext context, String template)
        throws TemplateException
    {
        return handleRequest(new ContextAdapter(context), template);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(Context context, String filename)
        throws TemplateException
    {
        return handleRequest(context, filename, (String)null, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public String handleRequest(Context context, String filename,
                                String charset, String encoding)
        throws TemplateException
    {
        String results = null;
        ByteArrayOutputStream bytes = null;

        try
        {
            bytes = new ByteArrayOutputStream();
            charset = decodeRequest(context, filename, bytes, charset,
                                    encoding);
            results = bytes.toString(charset);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
        finally
        {
            try
            {
                if (bytes != null)
                {
                    bytes.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
        return results;
    }

    /**
     * @see org.apache.fulcrum.template.TemplateEngineService
     */
    public void handleRequest(TemplateContext context, String template,
                              OutputStream outputStream)
        throws TemplateException
    {
        handleRequest(new ContextAdapter(context), template, outputStream);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public void handleRequest(Context context, String filename,
                              OutputStream output)
        throws TemplateException
    {
        handleRequest(context, filename, output, null, null);
    }

    /**
     * @see org.apache.fulcrum.velocity.VelocityService
     */
    public void handleRequest(Context context, String filename,
                              OutputStream output, String charset,
                              String encoding)
        throws TemplateException
    {
        decodeRequest(context, filename, output, charset, encoding);
    }

    /**
     * @see BaseTemplateEngineService#handleRequest(TemplateContext, String, Writer)
     */
    public void handleRequest(TemplateContext context,
                                       String template, Writer writer)
        throws TemplateException
    {
        handleRequest(new ContextAdapter(context), template, writer);
    }

    /**
     * @see VelocityService#handleRequest(Context, String, Writer)
     */
    public void handleRequest(Context context, String filename,
                              Writer writer)
        throws TemplateException
    {
        handleRequest(context, filename, writer, null);
    }

    /**
     * @see VelocityService#handleRequest(Context, String, Writer, String)
     */
    public void handleRequest(Context context, String filename,
                              Writer writer, String encoding)
        throws TemplateException
    {
        try
        {
            // If the context is not already an instance of
            // InternalEventContext, wrap it in a VeclocityContext so that
            // event cartridges will work. Unfortunately there is no interface
            // that extends both Context and InternalEventContext, so this
            // is not as clear as it could be.

            Context eventContext;

            if ( context instanceof InternalEventContext )
            {
                eventContext = context;
            }
            else
            {
                eventContext = new VelocityContext( context );
            }

            // Attach the EC to the context
            EventCartridge ec = getEventCartridge();
            if (ec != null && eventCartridgeEnabled)
            {
                ec.attachToContext(eventContext);
            }

            if (encoding != null)
            {
                // Request scoped encoding first supported by Velocity 1.1.
                velocityEngine.mergeTemplate(filename, encoding,
                                             eventContext, writer);
            }
            else
            {
                velocityEngine.mergeTemplate(filename, eventContext, writer);
            }
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
    }

    /**
     * By default, this is true if there is configured event cartridges.
     * You can disable EC processing if you first disable it and then call
     * handleRequest.
     */
    public void setEventCartridgeEnabled(boolean value)
    {
        this.eventCartridgeEnabled = value;
    }

    /**
     * @return EventCartridge the event cartridge
     */
    public EventCartridge getEventCartridge()
    {
        return eventCartridge;
    }

    /**
     * Processes the request and fill in the template with the values
     * you set in the the supplied Context. Applies the specified
     * character and template encodings.
     *
     * @param context A context to use when evaluating the specified
     * template.
     * @param filename The file name of the template.
     * @param output The stream to which we will write the processed
     * template as a String.
     * @return The character set applied to the resulting text.
     *
     * @throws ServiceException Any exception trown while processing
     * will be wrapped into a ServiceException and rethrown.
     */
    private String decodeRequest(Context context, String filename,
                                 OutputStream output, String charset,
                                 String encoding)
        throws TemplateException
    {
        // TODO: Push this method of getting character set & encoding
        // from RunData back into Turbine.
        // charset  = ((RunData) data).getCharSet();
        // encoding = ((RunData) data).getTemplateEncoding();

        if (charset == null)
        {
            charset = DEFAULT_CHAR_SET;
        }

        OutputStreamWriter writer = null;
        try
        {
            try
            {
                writer = new OutputStreamWriter(output, charset);
            }
            catch (Exception e)
            {
                renderingError(filename, e);
            }
            handleRequest(context, filename, writer, encoding);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return charset;
    }

    /**
     * Macro to handle rendering errors.
     *
     * @param filename The file name of the unrenderable template.
     * @param e        The error.
     *
     * @exception ServiceException Thrown every time.  Adds additional
     *                             information to <code>e</code>.
     */
    private final void renderingError(String filename, Throwable e)
        throws TemplateException
    {
        String err = "Error rendering Velocity template: " + filename;
        getLogger().error(err + ": " + e.getMessage());
        // if the Exception is a MethodInvocationException, the underlying
        // Exception is likely to be more informative, so rewrap that one.
        if (e instanceof MethodInvocationException)
        {
            e = ((MethodInvocationException)e).getWrappedThrowable();
        }

        throw new TemplateException(err, e);
    }

    /**
     * Find out if a given template exists. Velocity
     * will do its own searching to determine whether
     * a template exists or not.
     *
     * @param String template to search for
     * @return boolean
     */
    public boolean templateExists(String template)
    {
        return velocityEngine.templateExists(template);
    }

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void configure(Configuration conf)
        throws ConfigurationException
    {
        // put conf into object to pass to the velocity engine
        velocityConf =
            new org.apache.commons.configuration.BaseConfiguration();
        List ecconfig = null;
        String logPath = null;
        List templatePathKeys = new ArrayList();
        List templatePaths = new ArrayList();

        if (1==2)
        {
            /* ### FIXME: Why is this both setup in a block which
               ### won't be compiled, and commented out?
            org.apache.commons.configuration.Configuration oldConf =
                getConfiguration();

            ecconfig = oldConf
                .getVector("eventCartridge.classes", new Vector(0));
            if (ecconfig.isEmpty())
            {
                getLogger().info("No Velocity EventCartridges configured.");
            }

            // Now we have to perform a couple of path translations
            // for our log file and template paths.
            logPath =
                oldConf.getString(VelocityEngine.RUNTIME_LOG, null);
            if (logPath == null || logPath.length() == 0)
            {
                String msg = VelocityService.SERVICE_NAME+" runtime log file "+
                    "is misconfigured: '" + logPath + "' is not a valid log file";
                throw new Error(msg);
            }

            // Get all the template paths where the velocity
            // runtime should search for templates and
            // collect them into a separate vector
            // to avoid concurrent modification exceptions.
            for (Iterator i = oldConf.getKeys(); i.hasNext();)
            {
                String key = (String) i.next();
                if (key.endsWith(RESOURCE_LOADER_PATH))
                {
                    templatePathKeys.add(key);
                    templatePaths.add(oldConf.getVector(key));
                }
            }
            */
        }
        else
        {
            // trick compiler
            if (true)
            {
                throw new ConfigurationException(
                    "Use of avalon-style configuration not completed yet");
            }

            final Configuration eventCartridgeConfs =
                conf.getChild("event-cartriges", false);
            if (eventCartridgeConfs == null)
            {
                ecconfig = Collections.EMPTY_LIST;
            }
            else
            {
                Configuration[] classNameConfs =
                    eventCartridgeConfs.getChildren("classname");
                if (classNameConfs == null)
                {
                    ecconfig = Collections.EMPTY_LIST;
                }
                else
                {
                    ecconfig = new ArrayList(classNameConfs.length);
                    for (int i=0; i < classNameConfs.length; i++)
                    {
                        ecconfig.add(classNameConfs[i].getValue());
                    }
                }
            }

            /*
             *            final Configuration pathConfs =
             *                conf.getChild("event-cartriges", false);
             *            if (pathConfs != null)
             *            {
             *                Configuration[] nameVal = ecConfs.getChildren();
             *                for (int i=0; i < nameVal.length; i++)
             *                {
             *                    String key = nameVal[i].getName();
             *                    String val = nameVal[i].getValue();
             *                }
             *            }
             */
        }

        initEventCartridges(ecconfig);

        // check if path to logfile needs translation to webapp root
        if ( !(new File(logPath).isAbsolute()) )
        {
            logPath = getRealPath(logPath);
        }
        velocityConf.setProperty(VelocityEngine.RUNTIME_LOG, logPath);

        configureTemplatePaths(templatePathKeys, templatePaths);

        // Register with the template service.
        registerConfiguration(conf, "vm");
    }

    /**
     * This method is responsible for initializing the various Velocity
     * EventCartridges. You just add a configuration like this:
     * <code>
     * services.VelocityService.eventCartridge.classes = org.tigris.scarab.util.ReferenceInsertionFilter
     * </code>
     * and list out (comma separated) the list of EC's that you want to
     * initialize.
     */
    private void initEventCartridges(List ecconfig)
        throws ConfigurationException
    {
        eventCartridge = new EventCartridge();
        Object obj = null;
        String className = null;
        for (Iterator i = ecconfig.iterator() ; i.hasNext() ;)
        {
            className = (String)i.next();
            try
            {
                boolean result = false;

                obj = Class.forName(className).newInstance();

                if (obj instanceof ReferenceInsertionEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((ReferenceInsertionEventHandler)obj);
                }
                else if (obj instanceof NullSetEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((NullSetEventHandler)obj);
                }
                else if (obj instanceof MethodExceptionEventHandler)
                {
                    result = getEventCartridge()
                        .addEventHandler((MethodExceptionEventHandler)obj);
                }
                getLogger().info("Added EventCartridge: " +
                    obj.getClass().getName() + " : " + result);
            }
            catch (Exception h)
            {
                throw new ConfigurationException(
                    "Could not configure EventCartridge: " +
                    className, h);
            }
        }
    }

    /**
     * Setup the velocity runtime by using a subset of the
     * Turbine configuration which relates to velocity.
     *
     * @exception ConfigurationException For any errors during initialization.
     */
    private void configureTemplatePaths(
        List templatePathKeys, List templatePaths)
    {
        // Loop through all template paths, clear the corresponding
        // velocity properties and translate them all to the webapp space.
        for (int i=0; i<templatePathKeys.size(); i++)
        {
            String key = (String) templatePathKeys.get(i);
            Vector paths = (Vector) templatePaths.get(i);
            if (paths != null)
            {
                String entry;
                for (Iterator j = paths.iterator(); j.hasNext();)
                {
                    String path = (String) j.next();
                    if (path.startsWith(JAR_PREFIX + "file"))
                    {
                        // A local jar resource URL path is a bit more
                        // complicated, but we can translate it as well.
                        int ind = path.indexOf("!/");
                        if (ind >= 0)
                        {
                            entry = path.substring(ind);
                            path = path.substring(9,ind);
                        }
                        else
                        {
                            entry = "!/";
                            path = path.substring(9);
                        }
                        path = JAR_PREFIX + "file:" + getRealPath(path) +
                            entry;
                    }
                    else if (path.startsWith(ABSOLUTE_PREFIX))
                    {
                        path = path.substring (ABSOLUTE_PREFIX.length(),
                                               path.length());
                    }
                    else if (!path.startsWith(JAR_PREFIX))
                    {
                        // But we don't translate remote jar URLs.
                        path = getRealPath(path);
                    }
                    // Put the translated paths back to the configuration.
                    velocityConf.addProperty(key,path);
                }
            }
        }
    }

    // ---------------- Avalon Lifecycle Methods ---------------------

    /**
     * Avalon component lifecycle method
     */
    public void initialize()
        throws Exception
    {
        try
        {
            velocityEngine = new VelocityEngine();

            // clear the property to prepare for new value,
            //is this needed?
            Iterator i = velocityConf.getKeys();
            while (i.hasNext())
            {
                velocityEngine.clearProperty((String)i.next());
            }

            velocityEngine.setExtendedProperties(ConfigurationConverter
                    .getExtendedProperties(velocityConf));
            velocityEngine.init();
            velocityConf = null;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(
                "Failed to initialize DefaultVelocityService", e);
        }
    }

}
