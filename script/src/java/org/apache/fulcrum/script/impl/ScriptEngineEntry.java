package org.apache.fulcrum.script.impl;

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

import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

/**
 * An entry of the script engine.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class ScriptEngineEntry
{
    /** the name of the script engine */
    private String name;

    /** the default extension of the scripts */
    private String extension;

    /** is the script cached or loaded for each execution */
    private boolean isCached;

    /** is the script compiled to improve performance */
    private boolean isCompiled;

    /** the location of the scripts */
    private String location;

    /** the associated scripting engine */
    private ScriptEngine scriptEngine;

    /** a list of scripts to execute during initialization */
    private List preLoadedScripts;

    /** a list of scripts to execute during startup */
    private List startupScripts;

    /** a list of scripts to execute during shutdown */
    private List shutdownScripts;

    /** the script engine bindings after executing the pre-loaded scripts */
    private Bindings defaultBindings;

    /**
     * Constructor
     *
     * @param name the name of the script engine
     * @param extension the script extension, e.g. "js"
     * @param isCached is the script cached or loaded for each execution
     * @param isCompiled is the script compiled to improve performance
     * @param location the location of the scripts
     * @param scriptEngine the associated scripting engine
     */
    public ScriptEngineEntry(
        String name,
        String extension,
        boolean isCached,
        boolean isCompiled,
        String location,
        ScriptEngine scriptEngine)
    {
        Validate.notEmpty(name, "name");
        Validate.notEmpty(extension, "extension");
        Validate.notEmpty(location, "location");
        Validate.notNull(scriptEngine, "scriptEngine");

        this.name = name;
        this.extension = extension;
        this.isCached = isCached;
        this.isCompiled = isCompiled;
        this.location = location;
        this.scriptEngine = scriptEngine;
        this.defaultBindings = new SimpleBindings();
    }

    /**
     * @return Returns the isCached.
     */
    public boolean isCached()
    {
        return isCached;
    }

    /**
     * @return Returns the isCompiled.
     */
    public boolean isCompiled()
    {
        return isCompiled;
    }

    /**
     * @return Returns the location.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the script extension
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * @return Returns the scriptEngine.
     */
    public ScriptEngine getScriptEngine()
    {
        return scriptEngine;
    }

    /**
     * @return Returns the pre-loaded scripts.
     */
    public List getPreLoadedScripts()
    {
        return preLoadedScripts;
    }

    /**
     * @param scriptList The list of pre-loaded scripts
     */
    public void setPreloadScripts(List scriptList)
    {
        Validate.notNull(scriptList, "preLoadedScripts");
        this.preLoadedScripts = scriptList;
    }

    public List getStartupScripts()
    {
        return startupScripts;
    }

    public void setStartupScripts(List startupScripts)
    {
        Validate.notNull(startupScripts, "startupScripts");
        this.startupScripts = startupScripts;
    }

    public List getShutdownScripts()
    {
        return shutdownScripts;
    }

    public void setShutdownScripts(List shutdownScripts)
    {
        Validate.notNull(shutdownScripts, "shutdownScripts");
        this.shutdownScripts = shutdownScripts;
    }

    public Bindings getDefaultBindings()
    {
        return defaultBindings;
    }

    public void setDefaultBindings(Bindings defaultBindings)
    {
        this.defaultBindings = defaultBindings;
    }
}
