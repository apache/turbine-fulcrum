package org.apache.fulcrum.json.gson;

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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fulcrum.json.JsonService;
import org.apache.fulcrum.json.Rectangle;
import org.apache.fulcrum.json.TestClass;
import org.apache.fulcrum.testcontainer.BaseUnitTest;

/**
 * GSON JSON Test
 *
 * @author gk
 * @version $Id$
 */
public class GSONServiceTest extends BaseUnitTest
{
    private JsonService sc = null;
    private final String preDefinedOutput = 
        "{\"container\":{\"cf\":\"Config.xml\"},\"configurationName\":\"Config.xml\",\"name\":\"mytest\"}";

    /**
     * Constructor for test.
     *
     * @param testName name of the test being executed
     */
    public GSONServiceTest(String testName)
    {
        super(testName);
    }


    public void setUp() throws Exception
    {
        super.setUp();
        sc = (JsonService) this.lookup( JsonService.ROLE );

    }

    public void testSerialize() throws Exception
    {
        String serJson = sc.ser( new TestClass("mytest") );
        assertEquals("Encryption failed ", preDefinedOutput, serJson);
    }
    
    public void testDeSerialize() throws Exception
    {
        String serJson = sc.ser( new TestClass("mytest") );
        Object deson = sc.deSer( serJson, TestClass.class );
        assertEquals("Encryption failed ", TestClass.class, deson.getClass());
    }
    
    public void testSerializeExclude00() throws Exception
    {    
        String serJson = sc.serializeAllExceptFilter(  new TestClass("mytest"), (Class)null, (String[])null );
        assertEquals("Encryption failed ", "{\"container\":{\"cf\":\"Config.xml\"},\"configurationName\":\"Config.xml\",\"name\":\"mytest\"}", serJson);
    }
    
    public void testSerializeExcludeClass() throws Exception
    {    
        String serJson = sc.serializeAllExceptFilter(  new TestClass("mytest"),  String.class, (String[])null );
        assertEquals("Encryption failed ", "{\"container\":{}}", serJson);
    }
    
    public void testSerializeExcludeClassAndField() throws Exception
    {    
    	String serJson = sc.serializeAllExceptFilter(  new TestClass("mytest"),  String.class, "container" );
        System.out.println("serJson:"+ serJson);
        assertEquals("Encryption failed ", "{}", serJson);
    }
    
    public void testSerializeExcludeClassAndFields() throws Exception
    {    
    	String serJson = sc.serializeAllExceptFilter(  new TestClass("mytest"),  Map.class, "configurationName", "name" );
        assertEquals("Encryption failed ", "{}", serJson);
    }
    
    public void testSerializeExcludeField() throws Exception
    {    
   
    	String serJson = sc.serializeAllExceptFilter(  new TestClass("mytest"),  (Class)null, "configurationName" );
        assertEquals("Encryption failed ", "{\"container\":{\"cf\":\"Config.xml\"},\"name\":\"mytest\"}", serJson);
    }
    
    public void testSerializeDate() throws Exception
    {
        final SimpleDateFormat MMddyyyy = new SimpleDateFormat( "MM/dd/yyyy");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put( "date", Calendar.getInstance().getTime() );

        sc.setDateFormat( MMddyyyy );
        String serJson = sc.ser( map );  
        System.out.println("serJson:"+ serJson);
        assertTrue("Serialize with Adapater failed ", serJson.matches( "\\{\"date\":\"\\d\\d/\\d\\d/\\d{4}\"\\}" ));
    }
    
  public void testCollection() throws Exception
  {
      List<Rectangle> rectList = new ArrayList<Rectangle>();
      for ( int i = 0; i < 10; i++ )
      {
          Rectangle filteredRect = new Rectangle(i,i,"rect"+i);
          rectList.add( filteredRect ); 
      }
      String adapterSer = sc.ser( rectList );
      assertEquals("collect ser","[{'w':0,'h':0,'name':'rect0'},{'w':1,'h':1,'name':'rect1'},{'w':2,'h':2,'name':'rect2'},{'w':3,'h':3,'name':'rect3'},{'w':4,'h':4,'name':'rect4'},{'w':5,'h':5,'name':'rect5'},{'w':6,'h':6,'name':'rect6'},{'w':7,'h':7,'name':'rect7'},{'w':8,'h':8,'name':'rect8'},{'w':9,'h':9,'name':'rect9'}]",
                   adapterSer.replace( '"', '\'' )       );
  }
  
  public void testSerializeTypeAdapterForCollection() throws Exception
  {
      sc.addAdapter( "Collection Adapter",  ArrayList.class , TypeAdapterForCollection.class );
      List<Rectangle> rectList = new ArrayList<Rectangle>();
      for ( int i = 0; i < 10; i++ )
      {
          Rectangle filteredRect = new Rectangle(i,i,"rect"+i);
          rectList.add( filteredRect ); 
      }
      String adapterSer = sc.ser( rectList  );
      assertEquals("collect ser","{'rect0':0,'rect1':1,'rect2':4,'rect3':9,'rect4':16,'rect5':25,'rect6':36,'rect7':49,'rect8':64,'rect9':81}",
                   adapterSer.replace( '"', '\'' )       );
      
  }
  
  public void testDeserializationTypeAdapterForCollection() throws Exception
  {
      sc.addAdapter( "Collection Adapter",  ArrayList.class , TypeAdapterForCollection.class );
      List<Rectangle> rectList = new ArrayList<Rectangle>();
      for ( int i = 0; i < 10; i++ )
      {
          Rectangle filteredRect = new Rectangle(i,i,"rect"+i);
          rectList.add( filteredRect ); 
      }
      String adapterSer = sc.ser( rectList  );   
      ArrayList<Rectangle> resultList0 = sc.deSer( adapterSer, ArrayList.class );
      for ( int i = 0; i < 10; i++ )
      {
          assertEquals( "deser reread size failed" , ( i * i), resultList0.get( i ).getSize() );
      }      
  }
 
  public void testMixinAdapter() throws Exception
  {
        sc.addAdapter( "Test Adapter",  TestClass.class , TestJsonSerializer.class );
        String adapterSer = sc.ser( new TestClass("mytest") );
        assertEquals("failed adapter serialization:",
        "{\"n\":\"mytest\",\"p\":\"Config.xml\",\"c\":[]}",adapterSer);
  }  

}
