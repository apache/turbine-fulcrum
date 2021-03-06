package org.apache.fulcrum.groovy.impl;

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

import org.apache.commons.lang3.Validate;
import org.apache.fulcrum.groovy.GroovyRunnable;
import org.apache.fulcrum.groovy.GroovyService;

/**
 * A Runnable executing Groovy Scripts
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public class GroovyRunnableImpl implements GroovyRunnable {
	/** the name of the Groovy script to execute */
	private String scriptName;

	/** the service manager to locate the GroovyService */
	private GroovyService groovyScriptExecutor;

	/** the arguments to execute the Groovy script */
	private Object[] args;

	/** the result of the execution */
	private Object result;

	/** the execption thrown by the Groovy scipt if any */
	private Throwable throwable;

	/**
	 * Constructor
	 *
	 * @param scriptName           the name of the script to execute
	 * @param groovyScriptExecutor the service to execute the script
	 */
	public GroovyRunnableImpl(String scriptName, GroovyService groovyScriptExecutor) 
	{
		Validate.notEmpty(scriptName, "scriptName");
		Validate.notNull(groovyScriptExecutor, "groovyScriptExecutor");

		this.scriptName = scriptName;
		this.groovyScriptExecutor = groovyScriptExecutor;
		this.args = new Object[0];
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		try 
		{
			this.result = null;
			this.throwable = null;
			this.result = this.groovyScriptExecutor.execute(this.getScriptName(), this.getArgs());
		} 
		catch (Throwable t) 
		{
			this.throwable = t;
		}
	}

	/**
	 * @return Returns the exception.
	 */
	public Throwable getThrowable() 
	{
		return this.throwable;
	}

	/**
	 * @return Returns the result.
	 */
	public Object getResult() 
	{
		return result;
	}

	/**
	 * @return Returns the scriptName.
	 */
	public String getScriptName() 
	{
		return scriptName;
	}

	/**
	 * @param args The args to set.
	 */
	public void setArgs(Object[] args) 
	{
		Validate.notNull(args, "args");
		
		// deep copy to avoid FindBugs
		int size = args.length;
		Object[] data = new Object[size];
		for (int i = 0; i < data.length; i++) 
		{
			data[i] = args[i];
		}
		this.args = data;
	}

	/**
	 * @return Returns the args.
	 */
	public Object[] getArgs() 
	{
		int size = args.length;
		Object[] data = new Object[size];
		for (int i = 0; i < data.length; i++) 
		{
			data[i] = args[i];
		}
		return data;
	}

	/**
	 * @return was the execution successful?
	 */
	public boolean getIsSuccessful() 
	{
		return (this.getThrowable() == null ? true : false);
	}
}
