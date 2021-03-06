package org.apache.fulcrum.osworkflow.example.modules.actions;


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


import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This method is called by default when you load up the example webapp.  It redirects the
 * user to the Index.vm template.
 *
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class Index extends VelocityAction
{

    /**
     *  Default action is to load up the default screen (Index.vm) and list out the locations to go to:
     *  <ol>
     *    <li>Inventory Add Request</li>
     *    <li>Inventory Delete Request</li>
     *    <li>General Admin of Request</li>
     * </ol>
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(RunData data, Context context) throws Exception
    {

        log.debug("doPerform action event called");
        data.setScreenTemplate("Index.vm");

    }

}
