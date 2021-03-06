package org.apache.fulcrum.resourcemanager.impl;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.fulcrum.resourcemanager.ResourceManager;

/**
 * Base class for a service implementation capturing the Avalon configuration
 * artifacts
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */

public abstract class BaseResourceManager extends AbstractLogEnabled implements Contextualizable, Serviceable,
		Configurable, Initializable, Disposable, Reconfigurable, ResourceManager 
{
	
	/** the buffer size for copying streams */
	private static final int BUF_SIZE = 1024;

	/** The context supplied by the avalon framework */
	private Context context;

	/** The service manager supplied by the avalon framework */
	private ServiceManager serviceManager;

	/** The configuration supplied by the avalon framework */
	private Configuration configuration;

	/** the Avalon application directory */
	private File applicationDir;

	/** the Avalon temp directory */
	private File tempDir;

	/** the name of the domain */
	private String domain;

	/** the seed to generate the password */
	private String seed;

	/** use transparent encryption/decryption */
	private String useEncryption;

	/////////////////////////////////////////////////////////////////////////
	// Avalon Service Lifecycle Implementation
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor
	 */
	public BaseResourceManager() 
	{
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache
	 * .avalon.framework.context.Context)
	 */
	public void contextualize(Context context) throws ContextException 
	{
		this.context = context;
		this.applicationDir = (File) context.get("urn:avalon:home");
		this.tempDir = (File) context.get("urn:avalon:temp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.
	 * framework.service.ServiceManager)
	 */
	public void service(ServiceManager serviceManager) throws ServiceException 
	{
		this.serviceManager = serviceManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.avalon.framework.configuration.Configurable#configure(org.apache.
	 * avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration configuration) throws ConfigurationException 
	{
		this.configuration = configuration;
		this.setDomain(configuration.getAttribute("name"));
		this.seed = "resourcemanager";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception 
	{
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
	public void dispose() 
	{
		this.applicationDir = null;
		this.configuration = null;
		this.context = null;
		this.domain = null;
		this.seed = null;
		this.serviceManager = null;
		this.tempDir = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.avalon.framework.configuration.Reconfigurable#reconfigure(org.
	 * apache.avalon.framework.configuration.Configuration)
	 */
	public void reconfigure(Configuration configuration) throws ConfigurationException 
	{
		this.configure(configuration);
	}

	/////////////////////////////////////////////////////////////////////////
	// Service Implementation
	/////////////////////////////////////////////////////////////////////////

	/**
	 * @return Returns the configuration.
	 */
	protected Configuration getConfiguration() 
	{
		return this.configuration;
	}

	/**
	 * @return Returns the context.
	 */
	protected Context getContext() 
	{
		return this.context;
	}

	/**
	 * @return Returns the serviceManager.
	 */
	protected ServiceManager getServiceManager() 
	{
		return this.serviceManager;
	}

	/**
	 * @return Returns the applicationDir.
	 */
	protected File getApplicationDir() 
	{
		return applicationDir;
	}

	/**
	 * @return Returns the tempDir.
	 */
	protected File getTempDir() 
	{
		return tempDir;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() 
	{
		return domain;
	}

	/**
	 * Get the content as byte[].
	 * 
	 * @param content content to convert
	 * @return byte array representation of the object
	 * @throws IOException if unable to read
	 */
	protected byte[] getContent(Object content) throws IOException 
	{
		byte[] result = null;

		if (content instanceof String) 
		{
			result = ((String) content).getBytes();
		} 
		else if (content instanceof byte[]) 
		{
			result = (byte[]) content;
		} 
		else if (content instanceof InputStream) 
		{
			result = this.getBytes((InputStream) content);
		} 
		else if (content instanceof Properties) 
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			((Properties) content).store(baos, "Created by fulcrum-resourcemanager-service");
			result = baos.toByteArray();
		} 
		else 
		{
			String msg = "Don't know how to read " + content.getClass().getName();
			throw new IllegalArgumentException(msg);
		}

		return result;
	}

	/**
	 * Extract a byte[] from the input stream.
	 * 
	 * @param is input stream to read from
	 * @return byte array representation of the object
	 * @throws IOException if unable to read
	 */
	protected byte[] getBytes(InputStream is) throws IOException 
	{
		int ch;
		byte[] data = null;

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BufferedInputStream isReader = new BufferedInputStream(is);
		BufferedOutputStream osWriter = new BufferedOutputStream(os);

		while ((ch = isReader.read()) != -1) 
		{
			osWriter.write(ch);
		}

		osWriter.flush();
		data = os.toByteArray();
		osWriter.close();
		isReader.close();

		return data;
	}

	/**
	 * @param domain The domain to set.
	 */
	protected void setDomain(String domain) 
	{
		this.domain = domain;
	}

	/**
	 * @return Returns the useEncryption.
	 */
	protected String getUseEncryption() 
	{
		return useEncryption;
	}

	/**
	 * @param useEncryption The useEncryption to set.
	 */
	protected void setUseEncryption(String useEncryption) 
	{
		this.useEncryption = useEncryption;
	}


	/**
	 * Reads the given input stream and decrypts it if required
	 * 
	 * @param is the input stream to be read
	 * @return the content of the input stream
	 * @throws IOException if unable to read
	 */
	protected byte[] read(InputStream is) throws IOException 
	{
		return readPlain(is);
	}

	/**
	 * Reads from an unencrypted input stream
	 * 
	 * @param is the source input stream
	 * @return the content of the input stream
	 * @throws IOException if unable to read
	 */
	private byte[] readPlain(InputStream is) throws IOException 
	{
		byte[] result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.copy(is, baos);
		result = baos.toByteArray();
		return result;
	}

	/**
	 * Write the given output stream and encrypts it if required. If the encryption
	 * mode is "auto" we also encryt it.
	 *
	 * @param os      the output stream to be written
	 * @param content the content to be written
	 * @throws IOException if unable to read
	 */
	protected void write(OutputStream os, byte[] content) throws IOException 
	{
		writePlain(os, content);
	}

	/**
	 * Write the given content without encryption.
	 *
	 * @param os      the output stream
	 * @param content the content to be written
	 * @throws IOException if unable to read
	 */
	private void writePlain(OutputStream os, byte[] content) throws IOException 
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		this.copy(bais, os);
	}

	/**
	 * Pumps the input stream to the output stream.
	 *
	 * @param is the source input stream
	 * @param os the target output stream
	 * @throws IOException the copying failed
	 */
	private void copy(InputStream is, OutputStream os) throws IOException 
	{
		byte[] buf = new byte[BUF_SIZE];
		int n = 0;
		while ((n = is.read(buf)) > 0) 
		{
			os.write(buf, 0, n);
		}
		os.flush();
		os.close();
	}
}
