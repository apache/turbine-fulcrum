package org.apache.fulcrum;

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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Category;

/**
 * A base implementation of an {@link java.rmi.server.UnicastRemoteObject}
 * as a Turbine {@link org.apache.fulcrum.Service}.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 */
public class BaseUnicastRemoteService
    extends UnicastRemoteObject
    implements Service
{
    protected ExtendedProperties configuration;
    private boolean isInitialized;
    private String name;
    private ServiceBroker serviceBroker;

    public BaseUnicastRemoteService()
        throws RemoteException
    {
        isInitialized = false;
        name = null;
        serviceBroker = null;
    }

    /**
     * Returns the configuration of this service.
     *
     * @return The configuration of this service.
     */
    public ExtendedProperties getConfiguration()
    {
        if (name == null)
        {
            return null;
        }
        else
        {
            if (configuration == null)
            {
                configuration = getServiceBroker().getConfiguration(name);
            }
            return configuration;
        }
    }

    public void init()
        throws InitializationException
    {
        setInit(true);
    }

    protected void setInit(boolean value)
    {
        isInitialized = value;
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    /**
     * Shuts down this service.
     */
    public void shutdown()
    {
        setInit(false);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String setName()
    {
        return name;
    }

    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    public void setServiceBroker(ServiceBroker broker)
    {
        this.serviceBroker = broker;
    }

    public String getRealPath(String path)
    {
        return null;
    }

    public Category getCategory()
    {
        return null;
    }

    public Category getCategory(String name)
    {
        return null;
    }

    /**
     * Returns either <code>Initialized</code> or
     * <code>Uninitialized</code>, depending upon {@link
     * org.apache.fulcrum.Service} innitialization state.
     *
     * @see org.apache.fulcrum.Service#getStatus()
     */
    public String getStatus()
        throws ServiceException
    {
        return (isInitialized() ? "Initialized" : "Uninitialized");
    }
}
