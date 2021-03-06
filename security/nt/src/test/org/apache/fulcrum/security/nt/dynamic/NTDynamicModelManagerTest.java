package org.apache.fulcrum.security.nt.dynamic;

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

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.UserManager;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.User;
import org.apache.fulcrum.security.model.dynamic.DynamicModelManager;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.apache.fulcrum.testcontainer.BaseUnit4Test;

/**
 *
 * Test the NT implementation of the user manager. This test traps some
 * exceptions that can be thrown if there is NO nt dll.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: NTDynamicModelManagerTest.java 1374015 2012-08-16 19:48:54Z tv
 *          $
 */
public class NTDynamicModelManagerTest extends BaseUnit4Test implements TestConstants
{
    private DynamicModelManager modelManager;
    private SecurityService securityService;
    private UserManager userManager;
    private User user;

    @Before
    public void setUp() throws Exception
    {
        this.setRoleFileName("src/test/DynamicNTRoleConfig.xml");
        this.setConfigurationFileName("src/test/DynamicNTComponentConfig.xml");
        securityService = (SecurityService) lookup(SecurityService.ROLE);
        userManager = securityService.getUserManager();
        modelManager = (DynamicModelManager) securityService.getModelManager();
    }

    @Override
    @After
    public void tearDown()
    {
        user = null;
        userManager = null;
        securityService = null;
    }


    @Test
    public void testRevokeAll() throws Exception
    {
        try
        {
            user = userManager.getUserInstance("domain/BOB");
            user.setPassword("rob");
            modelManager.revokeAll(user);
            fail("Should throw runtime exception");
        }
        catch (DataBackendException dbe)
        {
            assertTrue(dbe.getMessage().indexOf(SCB_INVALID) > -1);
        }
        catch (UnknownEntityException re)
        {
            assertTrue(re.getMessage().indexOf("Unknown user") > -1);
        }
        catch (UnsatisfiedLinkError ule)
        {
            System.out.println("Unit test not being run due to missing NT DLL");
        }
    }

    @Test
    public void testGrantUserGroup() throws Exception
    {
        user = userManager.getUserInstance("domain/BOB");
        user.setPassword("rob");
        Group group = securityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        securityService.getGroupManager().addGroup(group);
        try
        {
            modelManager.grant(user, group);
            fail("Should throw runtime exception");
        }
        catch (UnknownEntityException re)
        {
            assertTrue(re.getMessage().indexOf("Unknown user") > -1);
        }
        catch (UnsatisfiedLinkError ule)
        {
            System.out.println("Unit test not being run due to missing NT DLL");
        }
        catch (java.lang.NoClassDefFoundError ncdfe)
        {
            System.out.println("Unit test not being run due to missing NT DLL");
        }
    }

    @Test
    public void testRevokeUserGroup() throws Exception
    {
        try
        {
            user = userManager.getUserInstance("domain/BOB");
            user.setPassword("rob");
            Group group = securityService.getGroupManager().getGroupInstance();
            group.setName("TEST_REVOKE");
            securityService.getGroupManager().addGroup(group);
            modelManager.revoke(user, group);
            fail("Should throw runtime exception");
        }
        catch (UnknownEntityException re)
        {
            assertTrue(re.getMessage().indexOf("Unknown user") > -1);
        }
        catch (UnsatisfiedLinkError ule)
        {
            System.out.println("Unit test not being run due to missing NT DLL");
        }
        catch (java.lang.NoClassDefFoundError ncdfe)
        {
            System.out.println("Unit test not being run due to missing NT DLL");
        }
    }
}
