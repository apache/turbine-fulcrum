/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
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
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
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
package org.apache.fulcrum.cache;
// Cactus and Junit imports

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnitTest;
/**
 * CacheTest
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @author <a href="epugh@upstate.com">Eric Pugh</a> 
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class CacheTest extends BaseUnitTest
{
    
    private GlobalCacheService globalCache = null;
    private static final String cacheKey = "CacheKey";
    private static final String cacheKey_2 = "CacheKey_2";
    public static final String SKIP_TESTS_KEY = "fulcrum.cache.skip.long.tests";
    

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public CacheTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        try
        {
            globalCache = (GlobalCacheService) this.lookup(GlobalCacheService.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /**
     * Simple test that verify an object can be created and deleted.
     * @throws Exception
     */
    public void testSimpleAddGetCacheObject() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject1 = null;
        // Create object
        cacheObject1 = new CachedObject(testString);
        assertNotNull("Failed to create a cachable object 1", cacheObject1);
        // Add object to cache
        globalCache.addObject(cacheKey, cacheObject1);
        // Get object from cache
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull("Did not retrieved a cached object 1", retrievedObject);
        assertTrue("Did not retrieved a correct, expected cached object 1", retrievedObject == cacheObject1);
        // Remove object from cache
        globalCache.removeObject(cacheKey);
        // Verify object removed from cache
        retrievedObject = null;
        cacheObject1 = null;
        try
        {
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull(
                "Retrieved the deleted cached object 1 and did not get expected ObjectExpiredException",
                retrievedObject);
            assertNotNull("Did not get expected ObjectExpiredException retrieving a deleted object", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull(
                "Retrieved the deleted cached object 1, but caught expected ObjectExpiredException exception",
                retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }
        // Remove object from cache that does NOT exist in the cache
        globalCache.removeObject(cacheKey);
    }
    /**
     * Simple test that adds, retrieves, and deletes 2 object.
     *
     * @throws Exception
     */
    public void test2ObjectAddGetCachedObject() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject1 = null;
        CachedObject cacheObject2 = null;
        // Create and add Object #1
        cacheObject1 = new CachedObject(testString);
        assertNotNull("Failed to create a cachable object 1", cacheObject1);
        globalCache.addObject(cacheKey, cacheObject1);
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull("Did not retrieved a cached object 1", retrievedObject);
        assertEquals("Did not retrieved correct cached object", cacheObject1, retrievedObject);
        // Create and add Object #2
        cacheObject2 = new CachedObject(testString);
        assertNotNull("Failed to create a cachable object 2", cacheObject2);
        globalCache.addObject(cacheKey_2, cacheObject2);
        retrievedObject = globalCache.getObject(cacheKey_2);
        assertNotNull("Did not retrieved a cached object 2", retrievedObject);
        assertEquals("Did not retrieved correct cached object 2", cacheObject2, retrievedObject);
        // Get object #1
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull("Did not retrieved a cached object 1. Attempt #2", retrievedObject);
        assertEquals("Did not retrieved correct cached object 1. Attempt #2", cacheObject1, retrievedObject);
        // Get object #1
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull("Did not retrieved a cached object 1. Attempt #3", retrievedObject);
        assertEquals("Did not retrieved correct cached object 1. Attempt #3", cacheObject1, retrievedObject);
        // Get object #2
        retrievedObject = globalCache.getObject(cacheKey_2);
        assertNotNull("Did not retrieved a cached object 2. Attempt #2", retrievedObject);
        assertEquals("Did not retrieved correct cached object 2 Attempt #2", cacheObject2, retrievedObject);
        // Remove objects
        globalCache.removeObject(cacheKey);
        globalCache.removeObject(cacheKey_2);
    }
    /**
     * Verify that an object will throw the ObjectExpiredException
     * when it now longer exists in cache.
     *
     * @throws Exception
     */
    public void testObjectExpiration() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject = null;
        // Create and add Object that expires in 1000 millis (1 second)
        cacheObject = new CachedObject(testString, 1000);
        assertNotNull("Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);
        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull("Did not retrieved a cached object", retrievedObject);
            assertEquals("Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertTrue("Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }
        // Sleep 1500 Millis (1.5 seconds)
        Thread.sleep(1500);
        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull(
                "Retrieved the expired cached object  and did not get expected ObjectExpiredException",
                retrievedObject);
            assertNotNull("Did not get expected ObjectExpiredException retrieving an expired object", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull(
                "Retrieved the expired cached object, but caught expected ObjectExpiredException exception",
                retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }
        // Remove objects
        globalCache.removeObject(cacheKey);
    }
    /**
     * Verify the all object will be flushed from the cache.
     *
     * This test can take server minutes.
     *
     * @throws Exception
     */
    public void testCacheFlush() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject = null;
        // Create and add Object that expires in 1 turbine Refresh + 1 millis 
        cacheObject = new CachedObject(testString, (getCacheRefresh() * 5) + 1);
        assertNotNull("Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);
        // 1 Refresh
        Thread.sleep(getCacheRefresh() + 1);
        assertTrue("No object in cache before flush", (0 < globalCache.getNumberOfObjects()));
        // Flush Cache
        globalCache.flushCache();
        // Wait 15 seconds, 3 Refresh
        Thread.sleep((getCacheRefresh() * 2) + 1);
        assertEquals("After refresh", 0, globalCache.getNumberOfObjects());
        // Remove objects
        globalCache.removeObject(cacheKey);
    }
    /**
     * Verify the Cache count is correct.
     * @throws Exception
     */
    public void testObjectCount() throws Exception
    {
        assertNotNull("Could not retrive cache service.", globalCache);
        // Create and add Object that expires in 1.5 turbine Refresh
        long expireTime = getCacheRefresh() + getCacheRefresh() / 2;
        CachedObject cacheObject = new CachedObject("This is a test", expireTime);
        assertNotNull("Failed to create a cachable object", cacheObject);
        globalCache.addObject(cacheKey, cacheObject);
        assertEquals("After adding 1 Object", 1, globalCache.getNumberOfObjects());
        // Wait until we're passed 1 refresh, but not half way.
        Thread.sleep(getCacheRefresh() + getCacheRefresh() / 3);
        assertEquals("After one refresh", 1, globalCache.getNumberOfObjects());
        // Wait until we're passed 2 more refreshes
        Thread.sleep((getCacheRefresh() * 2) + getCacheRefresh() / 3);
        assertEquals("After three refreshes", 0, globalCache.getNumberOfObjects());
    }
    /**
     * Verfy a refreshable object will refreshed in the following cases:
     * o The object is retrieved via getObject an it is stale.
     * o The object is determied to be stale during a cache
     *   refresh
     *
     * This test can take serveral minutes.
     *
     * @throws Exception
     */
    public void testRefreshableObject() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        RefreshableCachedObject cacheObject = null;
        // Create and add Object that expires in TEST_EXPIRETIME millis.
        cacheObject = new RefreshableCachedObject(new RefreshableObject(), getTestExpireTime());
        assertNotNull("Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);
        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull("Did not retrieved a cached object", retrievedObject);
            assertEquals("Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertTrue("Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }
        // Wait 1 Turbine cache refresh + 1 second.
        Thread.sleep(getTestExpireTime() + 1000);
        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull("Did not retrieved a cached object, after sleep", retrievedObject);
            assertNotNull(
                "Cached object has no contents, after sleep.",
                ((RefreshableCachedObject) retrievedObject).getContents());
            assertTrue(
                "Object did not refresh.",
                (((RefreshableObject) ((RefreshableCachedObject) retrievedObject).getContents()).getRefreshCount()
                    > 0));
        }
        catch (ObjectExpiredException e)
        {
            assertTrue(
                "Received unexpected ObjectExpiredException exception "
                    + "when retrieving refreshable object after ( "
                    + (System.currentTimeMillis() - addTime)
                    + " millis)",
                false);
        }
        catch (Exception e)
        {
            throw e;
        }
        // See if object will expires (testing every second for 100 seconds.  It sould not!
        for (int i = 0; i < 100; i++)
        {
            Thread.sleep(1000); // Sleep 0.5 seconds
            // Try to get expired object
            try
            {
                retrievedObject = null;
                retrievedObject = globalCache.getObject(cacheKey);
                assertNotNull("Did not retrieved a cached object, after sleep", retrievedObject);
                assertNotNull(
                    "Cached object has no contents, after sleep.",
                    ((RefreshableCachedObject) retrievedObject).getContents());
                assertTrue(
                    "Object did not refresh.",
                    (((RefreshableObject) ((RefreshableCachedObject) retrievedObject).getContents()).getRefreshCount()
                        > 0));
            }
            catch (ObjectExpiredException e)
            {
                assertTrue(
                    "Received unexpected ObjectExpiredException exception "
                        + "when retrieving refreshable object after ( "
                        + (System.currentTimeMillis() - addTime)
                        + " millis)",
                    false);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        // Remove objects
        globalCache.removeObject(cacheKey);
    }
    /**
     * Verify a cached object will be delete after it has been
     * untouched beyond it's TimeToLive.
     *
     * This test can take serveral minutes.
     *
     * @throws Exception
     */
    public void testRefreshableTimeToLive() throws Exception
    {
        String testString = new String("This is a test");
        Object retrievedObject = null;
        RefreshableCachedObject cacheObject = null;
        // Create and add Object that expires in TEST_EXPIRETIME millis.
        cacheObject = new RefreshableCachedObject(new RefreshableObject(), getTestExpireTime());
        assertNotNull("Failed to create a cachable object", cacheObject);
        cacheObject.setTTL(getTestExpireTime());
        // Verify TimeToLive was set
        assertEquals("Returned TimeToLive", getTestExpireTime(), cacheObject.getTTL());
        // Add object to Cache
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);
        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull("Did not retrieved a cached object", retrievedObject);
            assertEquals("Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            fail("Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)");
        }
        catch (Exception e)
        {
            throw e;
        }
        // Wait long enough to allow object to expire, but do not exceed TTL
        long timeout = getTestExpireTime() - 0000;
        Thread.sleep(timeout);
        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull("Did not retrieve a cached object, after sleep", retrievedObject);
            assertNotNull(
                "Cached object has no contents, after sleep.",
                ((RefreshableCachedObject) retrievedObject).getContents());
                /*
                 * @todo this is not working for some reason
                 
            assertTrue(
                "Object did not refresh.",
                (((RefreshableObject) ((RefreshableCachedObject) retrievedObject).getContents()).getRefreshCount()
                    > 0));
                    */
        }
        catch (ObjectExpiredException e)
        {
            assertTrue(
                "Received unexpected ObjectExpiredException exception "
                    + "when retrieving refreshable object after ( "
                    + (System.currentTimeMillis() - addTime)
                    + " millis)",
                false);
        }
        catch (Exception e)
        {
            throw e;
        }
        // Wait long enough to allow object to expire and exceed TTL
        Thread.sleep(getTestExpireTime() + 5000);
        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull("Retrieved a cached object, after exceeding TimeToLive", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull(
                "Retrieved the expired cached object, but caught expected ObjectExpiredException exception",
                retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }
    }
    /**
     * Test that we can get a list of the keys in the cache
     * 
     * @return
     */
    public void testCacheGetKeyList() {
        globalCache.flushCache();
        globalCache.addObject("date1", new CachedObject(new Date()));
        globalCache.addObject("date2", new CachedObject(new Date()));
        globalCache.addObject("date3", new CachedObject(new Date()));
        assertTrue("Did not get key list back.", (globalCache.getKeys() != null));
        List keys = globalCache.getKeys();
        for (Iterator itr = keys.iterator(); itr.hasNext();) {
            Object key = itr.next();
            assertTrue("Key was not an instance of String.", (key instanceof String));
        }
        
    }
    
    /**
     * Test that we can get a list of the keys in the cache
     * 
     * @return
     */
    public void testCacheGetCachedObjects() {
        globalCache.flushCache();
        globalCache.addObject("date1", new CachedObject(new Date()));
        globalCache.addObject("date2", new CachedObject(new Date()));
        globalCache.addObject("date3", new CachedObject(new Date()));
        assertTrue("Did not get object list back.", (globalCache.getCachedObjects() != null));
        List objects = globalCache.getCachedObjects();
        for (Iterator itr = objects.iterator(); itr.hasNext();) {
            Object obj = itr.next();
            assertNotNull("Object was null.", obj);
            assertTrue("Object was not an instance of CachedObject", (obj instanceof CachedObject));
        }
        
    }
    
    /**
     * Test that the retrieved list is safe from 
     * ConcurrentModificationException's being thrown if the cache
     * is updated while we are iterating over the List.
     * 
     * @return
     */
    public void testCacheModification() {
        globalCache.flushCache();
        globalCache.addObject("date1", new CachedObject(new Date()));
        globalCache.addObject("date2", new CachedObject(new Date()));
        globalCache.addObject("date3", new CachedObject(new Date()));
        assertTrue("Did not get key list back.", (globalCache.getKeys() != null));
        List keys = globalCache.getKeys();
        try {
	        for (Iterator itr = keys.iterator(); itr.hasNext();) {
	            Object key = itr.next();
	            globalCache.addObject("date4", new CachedObject(new Date()));           
	        }
        } catch (ConcurrentModificationException cme)
        {
            fail("Caught ConcurrentModificationException adding to cache.");
        }
        List objects = globalCache.getCachedObjects();
        try {
	        for (Iterator itr = objects.iterator(); itr.hasNext();) {
	            Object obj = itr.next();
	            globalCache.addObject("date4", new CachedObject(new Date()));           
	        }
        } catch (ConcurrentModificationException cme)
        {
            fail("Caught ConcurrentModificationException adding to cache.");
        }
    }
    
    
    /**
     * Down cast the interface to the concreate object in order to grab the 
     * cache check frequency.
     * @return the refresh requency in milliseconds
     */
    private long getCacheRefresh()
    {
        String skipTestsProperty = System.getProperty(SKIP_TESTS_KEY,"false");
        System.out.println("What is the skipTestsProperty:" + skipTestsProperty);
        if(Boolean.getBoolean(skipTestsProperty)==true){
            return 0;
        }
        else {
            return ((DefaultGlobalCacheService) globalCache).getCacheCheckFrequency()*1000;
        }
        
    }
    
    /** 
     * How long until it expires
     * @return the cache refresh plus 1000.
     */
    private long getTestExpireTime()
    {
        return getCacheRefresh() + 1000;
    }
    

	private long getTestTimeToLive()
	{
		return getTestExpireTime()*5;
	}
    /**
     * Simple object that can be refreshed
     */
    class RefreshableObject implements Refreshable
    {
        private int refreshCount = 0;
        /**
         * Increment the refresh counter
         */
        public void refresh()
        {
            this.refreshCount++;
        }
        /**
         * Reutrn the number of time this object has been refreshed
         *
         * @return Number of times refresh() has been called
         */
        public int getRefreshCount()
        {
            return this.refreshCount;
        }
    }
}
