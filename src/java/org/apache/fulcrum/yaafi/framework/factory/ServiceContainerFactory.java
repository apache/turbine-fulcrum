package org.apache.fulcrum.yaafi.framework.factory;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fulcrum.yaafi.framework.container.ServiceConstants;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;

/**
 * A factory to hide how to initialize YAFFI since this might change over the time
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 */

public class ServiceContainerFactory
{
    /** The logger to be used */
    private static Logger logger;

    /**
     * Create a fully initialized YAFFI service container
     * 
     * @param serviceManagerConfig the configuration to use
     * @throws Exception the creation failed
     */
    public static ServiceContainer create(
        ServiceContainerConfiguration serviceManagerConfig)
        throws Exception
    {
        Context context = serviceManagerConfig.createFinalContext();
        return ServiceContainerFactory.create( serviceManagerConfig, context );
    }

    /**
     * Create a fully initialized YAFFI service container
     * 
     * @param serviceManagerConfig the configuration to use
     * @param context the context to use
     * @throws Exception the creation failed
     */
    public static ServiceContainer create(
        ServiceContainerConfiguration serviceManagerConfig, Context context )
        throws Exception
    {
        String clazzName;
        Class clazz = null;
        Configuration configuration = null;
        ServiceContainer result = null;

        // Enforce a logger from the caller

        try
        {
            // bootstrap the logging

            ServiceContainerFactory.logger = serviceManagerConfig.getLogger();

            // bootstrap the configuration settings
            
            configuration = serviceManagerConfig.createFinalConfiguration();
            
            // bootstrap the service container

            clazzName = getServiceContainerClazzName(configuration);
            
            ServiceContainerFactory.logger.debug( 
                "Loading the service container class " + clazzName 
                );
            
            clazz = ServiceContainerFactory.class.getClassLoader().loadClass(
                clazzName 
                );

            ServiceContainerFactory.logger.debug( 
                "Instantiating the service container class " + clazzName 
                );

            result = (ServiceContainer) clazz.newInstance();
        }
        catch (Exception e)
        {
            String msg = "Creating the ServiceContainer failed";
            ServiceContainerFactory.logger.error( msg, e );
            throw e;
        }

        Logger serviceContainerLogger = serviceManagerConfig.getLogger();
        
        serviceContainerLogger.debug( 
            "Using the following configuration : " 
            + ConfigurationUtil.toString( configuration )
            );
        
        ContainerUtil.enableLogging( result, serviceManagerConfig.getLogger() );
        ContainerUtil.contextualize( result, context );
        ContainerUtil.configure( result, configuration );
        ContainerUtil.initialize( result );

        return result;
    }
    
    /**
     * Reads the implementation class of the YAAFI container
     */
    private static String getServiceContainerClazzName( Configuration configuration )
    {
        Configuration containerClazzNameConfig = configuration.getChild(
            ServiceConstants.CONTAINERCLAZZNAME_CONFIG_KEY
            );
               
        if( containerClazzNameConfig != null )
        {
            return containerClazzNameConfig.getValue(ServiceConstants.CLAZZ_NAME);
        }
        else
        {
            return ServiceConstants.CLAZZ_NAME;
        }
    }
    
}