package org.apache.fulcrum.yaafi.framework.crypto;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.fulcrum.jce.crypto.CryptoParameters;
import org.apache.fulcrum.jce.crypto.CryptoStreamFactoryImpl;
import org.apache.fulcrum.jce.crypto.CryptoUtil;
import org.apache.fulcrum.jce.crypto.HexConverter;
import org.apache.fulcrum.jce.crypto.PasswordFactory;

import junit.framework.TestCase;

/**
 * Test suite for crypto functionality
 *
 * @author <a href="mailto:siegfried.goeschl@drei.com">Siegfried Goeschl</a>
 * @version $Id$
 */

public class CryptoUtilTest extends TestCase
{   
    /** the password to be used */
    private String password;
    
    /** the test data directory */
    private File testDataDirectory;
    
    /** the temp data director */
    private File tempDataDirectory;
    
    /**
     * Constructor
     * @param name the name of the test case
     */
    public CryptoUtilTest( String name )
    {
        super(name);
        
        this.password = "mysecret";
        this.testDataDirectory = new File( "./src/test/data" );
        this.tempDataDirectory = new File( "./temp" );
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     *         byte[] salt,  
        int count, 
        String algorithm, 
        String providerName )

     */
    protected void setUp() throws Exception
    {
        CryptoStreamFactoryImpl factory = new CryptoStreamFactoryImpl(
            CryptoParameters.SALT,
            CryptoParameters.COUNT,
            "PBEWithMD5AndDES",
            CryptoParameters.PROVIDERNAME
            );
        
        CryptoStreamFactoryImpl.setInstance( factory );           
    }
    
    /**
     * @return Returns the password.
     */
    protected char[] getPassword()
    {
        return password.toCharArray();
    }

    /**
     * @return Returns the tempDataDirectory.
     */
    protected File getTempDataDirectory()
    {
        return tempDataDirectory;
    }
    
    /**
     * @return Returns the testDataDirectory.
     */
    protected File getTestDataDirectory()
    {
        return testDataDirectory;
    }

    /** Encrypt a text file */
    public void testTextEncryption() throws Exception
    {
        File sourceFile = new File( this.getTestDataDirectory(), "plain.txt" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.enc.txt" );
        CryptoUtil.encrypt( sourceFile, targetFile, this.getPassword() );
    }
    
    /** Decrypt a text file */
    public void testTextDecryption() throws Exception
    {
        testTextEncryption();
        File sourceFile = new File( this.getTempDataDirectory(), "plain.enc.txt" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.dec.txt" );
        CryptoUtil.decrypt( sourceFile, targetFile, this.getPassword() );        
    }    

    /** Encrypt an empty text file */
    public void testEmptyTextEncryption() throws Exception
    {
        File sourceFile = new File( this.getTestDataDirectory(), "empty.txt" );
        File targetFile = new File( this.getTempDataDirectory(), "empty.enc.txt" );
        CryptoUtil.encrypt( sourceFile, targetFile, this.getPassword() );
    }
    
    /** Decrypt a text file */
    public void testEmptyTextDecryption() throws Exception
    {
        testEmptyTextEncryption();
        File sourceFile = new File( this.getTempDataDirectory(), "empty.enc.txt" );
        File targetFile = new File( this.getTempDataDirectory(), "empty.dec.txt" );
        CryptoUtil.decrypt( sourceFile, targetFile, this.getPassword() );
    }    

    /** Encrypt a PDF file */
    public void testPdfEncryption() throws Exception
    {
        File sourceFile = new File( this.getTestDataDirectory(), "plain.pdf" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.enc.pdf" );
        CryptoUtil.encrypt( sourceFile, targetFile, this.getPassword() );
    }
    
    /** Decrypt a PDF file */
    public void testPdfDecryption() throws Exception
    {
        testPdfEncryption();
        File sourceFile = new File( this.getTempDataDirectory(), "plain.enc.pdf" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.dec.pdf" );
        CryptoUtil.decrypt( sourceFile, targetFile, this.getPassword() );
    }    

    /** Encrypt a ZIP file */
    public void testZipEncryption() throws Exception
    {
        File sourceFile = new File( this.getTestDataDirectory(), "plain.zip" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.enc.zip" );
        CryptoUtil.encrypt( sourceFile, targetFile, this.getPassword() );
    }
    
    /** Decrypt a ZIP file */
    public void testZipDecryption() throws Exception
    {
        testZipEncryption();
        File sourceFile = new File( this.getTempDataDirectory(), "plain.enc.zip" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.dec.zip" );
        CryptoUtil.decrypt( sourceFile, targetFile, this.getPassword() );
    }    

    /** Encrypt a XML file */
    public void testXmlEncryption() throws Exception
    {
        File sourceFile = new File( this.getTestDataDirectory(), "plain.xml" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.enc.xml" );        
        CryptoUtil.encrypt( sourceFile, targetFile, this.getPassword() );
    }
    
    /** Decrypt a XML file */
    public void testXMLDecryption() throws Exception
    {
        testXmlEncryption();
        File sourceFile = new File( this.getTempDataDirectory(), "plain.enc.xml" );
        File targetFile = new File( this.getTempDataDirectory(), "plain.dec.xml" );
        CryptoUtil.decrypt( sourceFile, targetFile, this.getPassword() );
    }  
    
    /** Test encryption and decryption of Strings */
    public void testStringEncryption() throws Exception
    {
        char[] testVector = new char[513];
        
        for( int i=0; i<testVector.length; i++ )
        {
            testVector[i] = (char) i;
        }
        
        String source = new String( testVector );
        String cipherText = CryptoUtil.encryptString( source, this.getPassword() );
        String plainText = CryptoUtil.decryptString( cipherText, this.getPassword() );
        assertEquals( source, plainText );                    
    }
    
    /** Test encryption and decryption of Strings */
    public void testStringHandling() throws Exception
    {
        String source = "Nobody knows the toubles I have seen ...";
        String cipherText = CryptoUtil.encryptString( source, this.getPassword() );
        String plainText = CryptoUtil.decryptString( cipherText, this.getPassword() );
        assertEquals( source, plainText );           
    }
    
    /** Test encryption and decryption of binary data */
    public void testBinaryHandling() throws Exception
    {
        byte[] source = new byte[256];
        byte[] result = null;
        
        for( int i=0; i<source.length; i++ )
        {
            source[i] = (byte) i;
        }

        ByteArrayOutputStream cipherText = new ByteArrayOutputStream();
        ByteArrayOutputStream plainText = new ByteArrayOutputStream();                
        
        CryptoUtil.encrypt( source, cipherText, this.getPassword() );
        CryptoUtil.decrypt( cipherText, plainText, this.getPassword() );
        
        result = plainText.toByteArray();        
        
        for( int i=0; i<source.length; i++ )
        {
            if( source[i] != result[i] )
            {
                fail( "Binary data are different" );
            }
        }           
    }   
    
    /** Test creating a password */
    public void testPasswordFactory() throws Exception
    {
        char[] result = null;        
        result = PasswordFactory.create();
        result = PasswordFactory.create( this.getPassword() );        
        return;
    }
    
    public void testHexConverter() throws Exception
    {
        String source = "DceuATAABWSaVTSIK";
        String hexString = HexConverter.toString( source.getBytes() );
        String result = new String( HexConverter.toBytes( hexString ) );
        assertEquals( source, result );
    }
}
