package org.apache.fulcrum.security.hibernate.dynamic;
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import net.sf.hibernate.avalon.HibernateService;

import org.apache.fulcrum.security.SecurityService;
import org
    .apache
    .fulcrum
    .security
    .model
    .dynamic
    .test
    .AbstractDynamicModelManagerTest;
import org.apache.fulcrum.security.hibernate.HibernateHelper;

/**
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class HibernateDynamicModelManagerTest
    extends AbstractDynamicModelManagerTest
{
    public void setUp()
    {
        try
        {
            this.setRoleFileName("src/test/DynamicHibernateRoleConfig.xml");
            this.setConfigurationFileName("src/test/DynamicHibernateComponentConfig.xml");
            HibernateService hibernateService =
                (HibernateService) lookup(HibernateService.ROLE);
            HibernateHelper.exportSchema(hibernateService.getConfiguration());
            securityService = (SecurityService) lookup(SecurityService.ROLE);
            super.setUp();

        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    public void tearDown()
    {
        try
        {
            //((BaseHibernateManager) permissionManager).getHibernateSession().close();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        securityService = null;
    }
    /**
    * Constructor for HibernatePermissionManagerTest.
    *
    * @param arg0
    */
    public HibernateDynamicModelManagerTest(String arg0)
    {
        super(arg0);
    }
}