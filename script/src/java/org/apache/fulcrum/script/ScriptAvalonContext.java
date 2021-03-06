package org.apache.fulcrum.script;

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

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Holder class for Avalon related artifacts to be passed to a script. This
 * allows full access to Avalon service framework from within the script.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface ScriptAvalonContext
{
    /**
     * Get the Avalon configuration.
     *
     * @return Returns the configuration
     */
    Configuration getConfiguration();

    /**
     * Get the Avalon context.
     *
     * @return Returns the context.
     */
    Context getContext();

    /**
     * Get the Avalon logger to access the logger from within the script.
     *
     * @return Returns the logger.
     */
    Logger getLogger();

    /**
     * Get the Avalon service manager to lookup services. 
     *
     * @return Returns the serviceManager.
     */
    ServiceManager getServiceManager();

    /**
     * @return Returns the parameters.
     */
    Parameters getParameters();

    /**
     * Get the working directory of the application.
     *
     * @return Returns the Avalon application directory
     * @throws ContextException the context entry for the application directory was not found
     */
    File getApplicationDir() throws ContextException;

    /**
     * Get the working directory of the application.
     *
     * @return Returns the Avalon application directory
     * @throws ContextException the context entry for the temp directory was not found
     */
    File getTempDir() throws ContextException;
}
