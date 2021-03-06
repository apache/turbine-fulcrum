package org.apache.fulcrum.yaafi.framework.interceptor;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.fulcrum.yaafi.framework.util.ToStringBuilder;
import org.apache.fulcrum.yaafi.framework.util.Validate;

/**
 * The InvocationHandler invoked when a service call is routed through
 * the dynamic proxy.
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl </a>
 */

public class AvalonInterceptorInvocationHandler implements InvocationHandler
{
    /** the name of the service */
    private String serviceName;

    /** the shorthand of the service */
    private String serviceShorthand;

    /** the real service implementation */
    private Object serviceDelegate;

    /** the list of interceptors to be invoked */
    private AvalonInterceptorService [] serviceInterceptorList;

    /** counts the current transactions */
    private static AtomicLong transactionCounter = new AtomicLong(0);

    /** the current transaction id */
    private Long transactionId;

    /**
     * Constructor.
     *
     * @param serviceName the name of the service
     * @param serviceShorthand the shorthand of the service being intercepted
     * @param serviceDelegate the real service implementation
     * @param serviceInterceptorList the list of interceptors to be invoked
     */
    public AvalonInterceptorInvocationHandler(
        String serviceName,
        String serviceShorthand,
        Object serviceDelegate,
        AvalonInterceptorService [] serviceInterceptorList )
    {
        Validate.notEmpty(serviceName,"serviceName");
        Validate.notEmpty(serviceShorthand,"serviceShorthand");
        Validate.notNull(serviceDelegate,"serviceDelegate");
        Validate.notNull(serviceInterceptorList,"serviceInterceptorList");

        this.serviceName = serviceName;
        this.serviceShorthand = serviceShorthand;
        this.serviceDelegate = serviceDelegate;
        this.serviceInterceptorList = serviceInterceptorList;
    }

    /**
     * @return Returns the delegate.
     */
    public Object getServiceDelegate()
    {
        return this.serviceDelegate;
    }

    /**
     * @return Returns the serviceInterceptorList.
     */
    public AvalonInterceptorService [] getServiceInterceptorList()
    {
        return serviceInterceptorList;
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @return Returns the serviceShorthand.
     */
    public String getServiceShorthand()
    {
        return serviceShorthand;
    }

    /**
     * @return Returns the transaction id
     */
    public Long getTransactionId()
    {
        return transactionId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        toStringBuilder.append("serviceShorthand",this.serviceShorthand);
        toStringBuilder.append("serviceName",this.serviceName);
        toStringBuilder.append("serviceDelegate",this.serviceDelegate);
        toStringBuilder.append("transactionId",this.transactionId);

        return toStringBuilder.toString();
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object [] args)
        throws Throwable
    {
        Object result;

        // create the interceptor context for current method call

        AvalonInterceptorContext context = new AvalonInterceptorContextImpl(
            this.getServiceName(),
            this.getServiceShorthand(),
            this.getServiceDelegate(),
            method,
            args
            );

        // if no transaction id is currently define we create a new one

        boolean hasCreatedTransaction = this.createTransactionId(context);

        try
        {
            context.incrementInvocationDepth();
            this.onEntry(context);
            result = method.invoke( this.getServiceDelegate(), args );
            this.onExit(context,result);
            return result;
        }
        catch (InvocationTargetException e)
        {
            this.onError(context,e.getTargetException());
            throw e.getTargetException();
        }
        catch (Throwable t)
        {
            this.onError(context,t);
            throw t;
        }
        finally
        {
            // decrement the service invocation depth

            context.decrementInvocationDepth();

            // reset the transaction id if we have created it before

            if( hasCreatedTransaction )
            {
                context.clearTransactionId();
            }
        }
    }

    /**
     * Invoke the onEntry method on all service interceptors.
     *
     * @param context the current interceptor context
     */
    private void onEntry( AvalonInterceptorContext context )
    {
        for( int i=0; i<this.getServiceInterceptorList().length; i++ )
        {
            AvalonInterceptorService avalonInterceptorService = this.getServiceInterceptorList()[i];
            avalonInterceptorService.onEntry(context);
        }
    }

    /**
     * Invoke the onExit method on all service interceptors.
     *
     * @param context the current interceptor context
     * @param result the result
     */
    private void onExit( AvalonInterceptorContext context, Object result )
    {
        for( int i=this.getServiceInterceptorList().length-1; i>=0; i-- )
        {
            AvalonInterceptorService avalonInterceptorService = this.getServiceInterceptorList()[i];
            avalonInterceptorService.onExit(context, result);
        }
    }

    /**
     * Invoke the onError method on all service interceptors.
     *
     * @param context the current interceptor context
     * @param t the resulting exception
     */
    private void onError( AvalonInterceptorContext context, Throwable t )
    {
        for( int i=this.getServiceInterceptorList().length-1; i>=0; i-- )
        {
            AvalonInterceptorService avalonInterceptorService = this.getServiceInterceptorList()[i];
            avalonInterceptorService.onError(context, t);
        }
    }

    /**
     * Creates a transaction id using the thread local storage
     * @param context current interceptor context
     * @return was a new transaction started
     */
    private boolean createTransactionId(AvalonInterceptorContext context)
    {
        Long currentTransactionId;

        if( !context.hasTransactionId() )
        {
            // create a new transaction id

            currentTransactionId = transactionCounter.incrementAndGet();
            this.transactionId = currentTransactionId;

            // store it in the TLS

            context.setTransactionId(currentTransactionId);

            return true;
        }

        return false;
    }
}
