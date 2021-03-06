package org.apache.fulcrum.commonsemail;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.fulcrum.commonsemail.impl.CommonsEmailUtils;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * CommonsEmailServiceTest
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
@DisplayName("CommonsEmailTest-Test")
public class CommonsEmailServiceTest extends BaseUnit5Test
{
    /** the service to test */
    private CommonsEmailService service;
    
    /** the default subject */
    private String subject;
    
    /** the default domain */
    private String domain;
    
    /** the recipient of the email */
    private String mailTo;

    /** the sender of the email */
    private String mailFrom;

    /** the generated MimeMessage */
    private MimeMessage result;
    
    /** default plain text content */
    private static final String PLAIN_CONTENT = "Hello World";
    
    /** default HTML text content */
    private static final String HTML_CONTENT = "<h1>Hello World</h1>";
    
    public static enum EmlType { original, overwrite, post, send };
    
    Logger log = LogManager.getLogger(getClass().getName());

    /**
     * Defines the testcase name for JUnit.
     */
    public CommonsEmailServiceTest()
    {
       
    }

    /**
     * Test setup
     */
    @BeforeEach
    public void setUp() throws Exception
    {
        
        this.domain 	= "test";
        this.subject 	= "CommonsEmailTest-Test";
        this.mailFrom 	= "demo@it20one.at";
        this.mailTo 	= "demo@it20one.at";

        try
        {
            service = (CommonsEmailService) this.lookup(CommonsEmailService.class.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    public void tearDown()
    {
        if( this.result != null )
        {            
            try
            {
                File resultDirectory = new File("./target/temp");
                resultDirectory.mkdirs();
                File resultFile = new File( resultDirectory, "CommonsEmailTest-Test"+".eml" );
                FileOutputStream fos = new FileOutputStream(resultFile);
                this.result.writeTo(fos);
                fos.flush();
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }            
        }
        
        super.tearDown();
    }
    
    /**
     * @return the CommonsEmailService service to be used
     */
    protected CommonsEmailService getService()
    {
        return this.service;
    }
    
    /**
     * @return Returns the mail subject.
     */
    protected String getSubject()
    {
        return subject;
    }
        
    /**
     * @return Returns the domain name.
     */
    protected String getDomain()
    {
        return domain;
    }
    
    /**
     * @return Returns the mailTo.
     */
    protected String getMailTo()
    {
        return mailTo;
    }
    
    /**
     * @return Returns the mailFrom.
     */
    protected String getMailFrom()
    {
        return mailFrom;
    }
    
    /**
     * @return a preconfigured attachment
     */
    protected EmailAttachment getEmailAttachment()
    {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath("./src/test/TestComponentConfig.xml");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setName("TestComponentConfig.xml");
        attachment.setDescription("TestComponentConfig.xml");
        
        return attachment;        
    }
    /////////////////////////////////////////////////////////////////////////
    // Start of unit tests
    /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple email and send it.
     *
     * @throws Exception the test failed 
     */
    @Test
    public void testSimpleEmail() throws Exception
    {
        SimpleEmail email = this.getService().createSimpleEmail(this.getDomain());
        
        email.setSubject(this.getSubject());        
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());
        
        this.result = this.getService().send(this.getDomain(),email);
    }

    /**
     * Create a HTML email and send it.
     *
     * @throws Exception the test failed 
     */
    @Test
    public void testHtmlEmail() throws Exception
    {
        HtmlEmail email = this.getService().createHtmlEmail(this.getDomain());
        
        email.setSubject(this.getSubject());        
        email.setTextMsg(PLAIN_CONTENT);
        email.setHtmlMsg(HTML_CONTENT);        
        email.addTo(this.getMailTo());
        
        this.result = this.getService().send(this.getDomain(),email);                
    }
    
    /**
     * Create a MultiPart email and send it.
     *
     * @throws Exception the test failed 
     */
    @Test
    public void testMultiPartEmail() throws Exception
    {
        MultiPartEmail email = this.getService().createMultiPartEmail(this.getDomain());                       
        EmailAttachment attachment = this.getEmailAttachment();
        
        email.setSubject(this.getSubject());
        email.attach(attachment);                
        email.addTo(this.getMailTo());
        email.setMsg(PLAIN_CONTENT);

        this.result = this.getService().send(this.getDomain(),email);
    }           
    
    /**
     * Use an undefined domain therefore reverting to the default domain.
     * 
     * @throws Exception the test failed
     */
    @Test
    public void testDefaultDomain() throws Exception
    {
        SimpleEmail email = this.getService().createSimpleEmail("grmpff");
        
        email.setSubject(this.getSubject());        
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());
        
        this.result = this.getService().send(this.getDomain(),email);       
    }
    
    /**
     * We pass "demo@it20one.at" therefore we should get the "it20one.at" domain
     * 
     * @throws Exception the test failed
     */
    @Test
    public void testDerivedDomain() throws Exception
    {
        SimpleEmail email = this.getService().createSimpleEmail("demo@it20one.at");
        
        email.setFrom(this.getMailFrom());
        email.setSubject(this.getSubject());        
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());
        
        this.result = this.getService().send(email);       
    }
    
    /**
     * Create a HTML email using a Hashtable as input.
     * 
     * @throws Exception the test failed
     */
    @Test
    public void testHtmlEmailWithHashtable() throws Exception
    {
        Vector attachments = new Vector();
        EmailAttachment attachment = this.getEmailAttachment();
        attachments.add(attachment);
        attachments.add(attachment);

        Hashtable content = new Hashtable();
        content.put(EmailConstants.EMAIL_SUBJECT, this.getSubject());
        content.put(EmailConstants.EMAIL_BODY, HTML_CONTENT);
        content.put(EmailConstants.SENDER_EMAIL, this.getMailFrom());
        content.put(EmailConstants.RECEIVER_EMAIL, this.getMailTo());
        content.put(EmailConstants.ATTACHMENTS, attachments);
        
        HtmlEmail email = this.getService().createHtmlEmail(
            this.getMailFrom(),
            content
            );
        
        this.result = this.getService().send(email);       
    }
    
    /**
     * Create a simple email using a Hashtable as input.
     * 
     * @throws Exception the test failed
     */
    @Test
    public void testSimpleEmailWithHashtable() throws Exception
    {
        Hashtable content = new Hashtable();
        content.put(EmailConstants.EMAIL_SUBJECT, this.getSubject());
        content.put(EmailConstants.EMAIL_BODY, PLAIN_CONTENT);
        content.put(EmailConstants.SENDER_EMAIL, this.getMailFrom());
        content.put(EmailConstants.RECEIVER_EMAIL, this.getMailTo());
        
        SimpleEmail email = this.getService().createSimpleEmail(
            this.getMailFrom(),
            content
            );
        
        this.result = this.getService().send(email);       
    }    
    
    /**
     * Create an email and send it to a bogus mailserver
     * resulting in an EmailException. For this test we
     * overwrite the SMTP server and port taken from the
     * domain configuration.
     *
     * @throws Exception the test failed
     */
    @Test
    public void testSendEmailToUnknownServer() throws Exception
    {
        Hashtable<String, String> content = new Hashtable<String, String>();
        content.put(EmailConstants.EMAIL_SUBJECT, this.getSubject());
        content.put(EmailConstants.EMAIL_BODY, PLAIN_CONTENT);
        content.put(EmailConstants.SENDER_EMAIL, this.getMailFrom());
        content.put(EmailConstants.RECEIVER_EMAIL, this.getMailTo());
        content.put(EmailConstants.MAIL_HOST, "localhost");
        content.put(EmailConstants.MAIL_PORT, "63178");
        
        SimpleEmail email = this.getService().createSimpleEmail(
            this.getMailFrom(),
            content
            );
        
        try
        {
            this.result = this.getService().send(email);
            
            if( this.getService().isMailDoNotSend(email.getFromAddress().getAddress()) == false )
            {
                fail();
            }
        }
        catch( EmailException e )
        {
            // expected
        }
    }
    
    /**
     * Create a mail session and simple MimeMessage and sent it.
     *  
     * @throws Exception the test failed
     */
    @Test
    public void testCreateMimeMessageWithSession() throws Exception
    {
        MimeMessage mimeMessage;
        Session session = this.getService().createSmtpSession("test","foo","bar");
        
        mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(this.mailFrom));
        mimeMessage.setSubject(this.getSubject());
        mimeMessage.setText(PLAIN_CONTENT);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(this.mailTo));

        this.result = this.getService().send(
            session,
            mimeMessage
            );
        
        assertNotNull( this.result.getMessageID() );
        
    }
    
    /**
     * Create a mail session and simple MimeMessage and sent it.
     *  
     * @throws Exception the test failed
     */
    @Test
    public void testReadMimeMessageWithSession() throws Exception
    {
        MimeMessage mimeMessage;
        // domain to read from global
        Session session = this.getService().createSmtpSession("test","foo","bar");
        
        mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(this.mailFrom));
        mimeMessage.setSubject(this.getSubject());
        mimeMessage.setText(PLAIN_CONTENT);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(this.mailTo));

        // domain is derived from javax.mail.internet.MimeMessage.getFrom()
        this.result = this.getService().send(
            session,
            mimeMessage
            );
      
        if (this.getService().getDomain("test").isMailDump()) {  
  
            for (EmlType et :EmlType.values()) {
                String name = "CommonsEmailService_" + et  + ".eml";
                File dumpFile = new File( this.getService().getServiceTempDir(), name );
                if (dumpFile.exists()) {
                    log.trace("reading from dump file {}", dumpFile.getAbsoluteFile().toString());
                    MimeMessage tempMessage = CommonsEmailUtils.readMimeMessage(session, dumpFile);
                    assertEquals("true",tempMessage.getSession().getProperty("mail.smtp.auth"));
                    // messageIds are not equal as result has a fake id with timestamp
                    System.out.println(tempMessage.getMessageID());
                    assertNotNull( tempMessage.getMessageID() );
                }
            }
           
        }
        
    }
    
    /**
     * Use commons-email to build a MimeMessage and send it directly.
     * 
     * @throws Exception the test failed
     */
    @Test
    public void testSendMimeMessage() throws Exception
    {
        MimeMessage mimeMessage;
        SimpleEmail email = this.getService().createSimpleEmail(this.getDomain());
        
        email.setSubject(this.getSubject());        
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());
        
        email.buildMimeMessage();
        mimeMessage = email.getMimeMessage();
        
        this.result = this.getService().send(
            email.getMailSession(),
            mimeMessage
            );

        assertNotNull( this.result.getMessageID() );
    }

    /**
     * Use commons-email to build a MimeMessage and send it directly but
     * without specifying a domain.
     *
     * @throws Exception the test failed
     */
    @Test
    public void testSendMimeMessageWithoutDomain() throws Exception
    {
        SimpleEmail email = this.getService().createSimpleEmail("siegfried.goeschl@it20one.at");

        email.setSubject(this.getSubject());
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());

        this.result = this.getService().send(
            email
            );

        assertNotNull( this.result.getMessageID() );
    }

    /**
     * Create a simple email, send it and verify deliver status.
     *
     * @throws Exception the test failed
     */
    @Test
    public void testGetSendDeliveryStatus() throws Exception
    {
        SimpleEmail email = this.getService().createSimpleEmail(this.getDomain());

        email.setSubject(this.getSubject());
        email.setMsg(PLAIN_CONTENT);
        email.addTo(this.getMailTo());
        email.addCc(this.getMailTo());
        email.addBcc(this.getMailTo());

        this.result = this.getService().send(this.getDomain(),email);

        SendDeliveryStatus sendDeliveryStatus = this.getService().getSendDeliveryStatus( this.result );
        assertTrue( sendDeliveryStatus.hasSucceeded() );
        assertTrue( sendDeliveryStatus.getValidSentAddresses().length >= 1);
        assertTrue( sendDeliveryStatus.size() >= 0);
    }

    /**
     * A test for a failed email delivery. The result depends heavily on
     * you mail server setup so there is no way to check for success ... :-(
     *
     * @throws Exception the test failed
     */
    @Test
    public void testGetSendDeliveryStatusFailure() throws Exception
    {
        MimeMessage mimeMessage;
        SimpleEmail email = this.getService().createSimpleEmail(this.getDomain());

        email.setSubject(this.getSubject());
        email.setMsg(PLAIN_CONTENT);
        email.addTo("foo@bar.com");

        email.buildMimeMessage();
        mimeMessage = email.getMimeMessage();

        try
        {
            this.result = this.getService().send(
                this.getDomain(),
                email.getMailSession(),
                mimeMessage
                );
 
            SendDeliveryStatus sendDeliveryStatus = this.getService().getSendDeliveryStatus( this.result );
            assertTrue( sendDeliveryStatus.hasSucceeded() );
            assertTrue( sendDeliveryStatus.getValidSentAddresses().length >= 1);
        }
        catch(Exception e)
        {
            SendDeliveryStatus sendDeliveryStatus = this.getService().getSendDeliveryStatus( mimeMessage );
            assertNotNull( sendDeliveryStatus );
            assertFalse( sendDeliveryStatus.hasSucceeded() );
        }
    }
}
