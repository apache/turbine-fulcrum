package org.apache.fulcrum.xmlrpc;

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

import org.apache.avalon.framework.component.Component;

/**
 * Description of the class.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface TestHandler
        extends Component
{
    public static String ROLE = TestHandler.class.getName();

    /**
     * Returns the passed parameter.
     *
     * @param message  The message to be returned
     * @return  The message passed
     */
    public String echo(String message);
}