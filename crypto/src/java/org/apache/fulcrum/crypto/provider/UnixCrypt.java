package org.apache.fulcrum.crypto.provider;

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

import java.util.Random;

import org.apache.fulcrum.crypto.CryptoAlgorithm;

/**
 * Implements Standard Unix crypt(3) for use with the Crypto Service.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class UnixCrypt implements CryptoAlgorithm 
{

	/** The seed to use */
	private String seed = null;

	/** standard Unix crypt chars (64) */
	private static final char[] SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./"
			.toCharArray();

	/**
	 * Constructor
	 */
	public UnixCrypt() 
	{
	}

	/**
	 * This class never uses anything but UnixCrypt, so it is just a dummy (Fixme:
	 * Should we throw an exception if something is requested that we don't support?
	 *
	 * @param cipher Cipher (ignored)
	 */
	public void setCipher(String cipher) 
	{
		/* dummy */
	}

	/**
	 * Setting the seed for the UnixCrypt algorithm. If a null value is supplied, or
	 * no seed is set, then a random seed is used.
	 *
	 * @param seed The seed value to use.
	 */
	public void setSeed(String seed) 
	{
		this.seed = seed;
	}

	/**
	 * Encrypt the supplied string with the requested cipher
	 *
	 * @param value The value to be encrypted
	 * @return The encrypted value
	 * @throws Exception An Exception of the underlying implementation.
	 */
	public String encrypt(String value) throws Exception 
	{
		if (seed == null) 
		{
			Random randomGenerator = new Random();
			int numSaltChars = SALT_CHARS.length;
			StringBuilder sb = new StringBuilder();
			sb.append(SALT_CHARS[Math.abs(randomGenerator.nextInt() % numSaltChars)])
					.append(SALT_CHARS[Math.abs(randomGenerator.nextInt() % numSaltChars)]).toString();
			seed = sb.toString();
		}

		// use commons-codec to do the encryption
		return org.apache.commons.codec.digest.UnixCrypt.crypt(value, seed);
	}
}
