package org.apache.fulcrum.security.hibernate;
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

import net.sf.hibernate.Session;

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class PersistenceHelperDefaultImplTest extends BaseUnitTest
{

   
    /**
    	   * Constructor for HibernatePermissionManagerTest.
    	   * @param arg0
    	   */
    public PersistenceHelperDefaultImplTest(String arg0)
    {
        super(arg0);
    }

    public void testPassingInExternalHibernateService() throws Exception
    {
        this.setRoleFileName("src/test/PersistenceHelperDefaultImplRoleConfig.xml");
        this.setConfigurationFileName("src/test/PersistenceHelperDefaultImplComponentConfig.xml");
       
		SecurityService securityService = (SecurityService) lookup(SecurityService.ROLE);
		HibernateGroupManagerImpl groupManager = (HibernateGroupManagerImpl)securityService.getGroupManager();
        PersistenceHelper persistenceHelper = groupManager.getPersistenceHelper();
        assertTrue(persistenceHelper instanceof PersistenceHelperDefaultImpl);
        PersistenceHelperDefaultImpl persistenceHelperFromGroupManager = (PersistenceHelperDefaultImpl)persistenceHelper;
        Session s = persistenceHelper.retrieveSession();
		
    }
    
    
}
