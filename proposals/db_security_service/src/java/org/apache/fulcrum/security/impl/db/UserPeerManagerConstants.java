package org.apache.fulcrum.security.impl.db;

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

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.db.userPeer.class = org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer
 * security.db.userPeer.column.name       = LOGIN_NAME
 * security.db.userPeer.column.id         = USER_ID
 * security.db.userPeer.column.password   = PASSWORD_VALUE
 * security.db.userPeer.column.firstname  = FIRST_NAME
 * security.db.userPeer.column.lastname   = LAST_NAME
 * security.db.userPeer.column.email      = EMAIL
 * security.db.userPeer.column.confirm    = CONFIRM_VALUE
 * security.db.userPeer.column.createdate = CREATED
 * security.db.userPeer.column.lastlogin  = LAST_LOGIN
 * security.db.userPeer.column.objectdata = OBJECTDATA
 * 
 * security.db.user.class = org.apache.fulcrum.security.impl.db.entity.TurbineUser
 * security.db.user.property.name       = UserName
 * security.db.user.property.id         = UserId
 * security.db.user.property.password   = Password
 * security.db.user.property.firstname  = FirstName
 * security.db.user.property.lastname   = LastName
 * security.db.user.property.email      = Email
 * security.db.user.property.confirm    = Confirmed
 * security.db.user.property.createdate = CreateDate
 * security.db.user.property.lastlogin  = LastLogin
 * security.db.user.property.objectdata = Objectdata
 *
 * </pre>
 * If security.db.user.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface UserPeerManagerConstants
{
    /** The key within the security service properties for the user peer class implementation */
    public static final String USER_CLASS_KEY = 
        "db.user.class";

    /** The key within the security service properties for the user peer class implementation */
    public static final String USER_PEER_CLASS_KEY = 
        "db.userPeer.class";

    /** User peer default class */
    public static final String USER_PEER_CLASS_DEFAULT =
        "org.apache.fulcrum.security.impl.db.entity.TurbineUserPeer";


    /** The column name for the login name field. */
    public static final String USER_NAME_COLUMN_KEY =
        "db.userPeer.column.name";

    /** The column name for the id field. */
    public static final String USER_ID_COLUMN_KEY =
        "db.userPeer.column.id";

    /** The column name for the password field. */
    public static final String USER_PASSWORD_COLUMN_KEY =
        "db.userPeer.column.password";

    /** The column name for the first name field. */
    public static final String USER_FIRST_NAME_COLUMN_KEY =
        "db.userPeer.column.firstname";

    /** The column name for the last name field. */
    public static final String USER_LAST_NAME_COLUMN_KEY =
        "db.userPeer.column.lastname";

    /** The column name for the email field. */
    public static final String USER_EMAIL_COLUMN_KEY =
        "db.userPeer.column.email";

    /** The column name for the confirm field. */
    public static final String USER_CONFIRM_COLUMN_KEY =
        "db.userPeer.column.confirm";

    /** The column name for the create date field. */
    public static final String USER_CREATE_COLUMN_KEY =
        "db.userPeer.column.createdate";

    /** The column name for the last login field. */
    public static final String USER_LAST_LOGIN_COLUMN_KEY =
        "db.userPeer.column.lastlogin";

    /** The column name for the objectdata field. */
    public static final String USER_OBJECTDATA_COLUMN_KEY =
        "db.userPeer.column.objectdata";


    /** The default value for the column name constant for the login name field. */
    public static final String USER_NAME_COLUMN_DEFAULT =
        "LOGIN_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String USER_ID_COLUMN_DEFAULT =
        "USER_ID";

    /** The default value for the column name constant for the password field. */
    public static final String USER_PASSWORD_COLUMN_DEFAULT =
        "PASSWORD_VALUE";

    /** The default value for the column name constant for the first name field. */
    public static final String USER_FIRST_NAME_COLUMN_DEFAULT =
        "FIRST_NAME";

    /** The default value for the column name constant for the last name field. */
    public static final String USER_LAST_NAME_COLUMN_DEFAULT =
        "LAST_NAME";

    /** The default value for the column name constant for the email field. */
    public static final String USER_EMAIL_COLUMN_DEFAULT =
        "EMAIL";

    /** The default value for the column name constant for the confirm field. */
    public static final String USER_CONFIRM_COLUMN_DEFAULT =
        "CONFIRM_VALUE";

    /** The default value for the column name constant for the create date field. */
    public static final String USER_CREATE_COLUMN_DEFAULT =
        "CREATED";

    /** The default value for the column name constant for the last login field. */
    public static final String USER_LAST_LOGIN_COLUMN_DEFAULT =
        "LAST_LOGIN";

    /** The default value for the column name constant for the objectdata field. */
    public static final String USER_OBJECTDATA_COLUMN_DEFAULT =
        "OBJECTDATA";

    /** The property name of the bean property for the login name field. */
    public static final String USER_NAME_PROPERTY_KEY = 
        "db.user.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String USER_ID_PROPERTY_KEY =
        "db.user.property.id";
    
    /** The property name of the bean property for the password field. */
    public static final String USER_PASSWORD_PROPERTY_KEY =
        "db.user.property.password";
    
    /** The property name of the bean property for the first name field. */
    public static final String USER_FIRST_NAME_PROPERTY_KEY =
        "db.user.property.firstname";
    
    /** The property name of the bean property for the last name field. */
    public static final String USER_LAST_NAME_PROPERTY_KEY =
        "db.user.property.lastname";
    
    /** The property name of the bean property for the email field. */
    public static final String USER_EMAIL_PROPERTY_KEY =
        "db.user.property.email";
    
    /** The property name of the bean property for the confirm field. */
    public static final String USER_CONFIRM_PROPERTY_KEY =
        "db.user.property.confirm";
    
    /** The property name of the bean property for the create date field. */
    public static final String USER_CREATE_PROPERTY_KEY =
        "db.user.property.createdate";
    
    /** The property name of the bean property for the last login field. */
    public static final String USER_LAST_LOGIN_PROPERTY_KEY =
        "db.user.property.lastlogin";

    /** The property name of the bean property for the last login field. */
    public static final String USER_OBJECTDATA_PROPERTY_KEY =
        "db.user.property.objectdata";

    /** The default value of the bean property for the login name field. */
    public static final String USER_NAME_PROPERTY_DEFAULT =
        "UserName";

    /** The default value of the bean property for the id field. */
    public static final String USER_ID_PROPERTY_DEFAULT =
        "UserId";

    /** The default value of the bean property for the password field. */
    public static final String USER_PASSWORD_PROPERTY_DEFAULT =
        "Password";

    /** The default value of the bean property for the first name field. */
    public static final String USER_FIRST_NAME_PROPERTY_DEFAULT =
        "FirstName";

    /** The default value of the bean property for the last name field. */
    public static final String USER_LAST_NAME_PROPERTY_DEFAULT =
        "LastName";

    /** The default value of the bean property for the email field. */
    public static final String USER_EMAIL_PROPERTY_DEFAULT =
        "Email";

    /** The default value of the bean property for the confirm field. */
    public static final String USER_CONFIRM_PROPERTY_DEFAULT =
        "Confirmed";

    /** The default value of the bean property for the create date field. */
    public static final String USER_CREATE_PROPERTY_DEFAULT =
        "CreateDate";

    /** The default value of the bean property for the last login field. */
    public static final String USER_LAST_LOGIN_PROPERTY_DEFAULT =
        "LastLogin";

    /** The default value of the bean property for the objectdata field. */
    public static final String USER_OBJECTDATA_PROPERTY_DEFAULT =
        "Objectdata";
};

    
