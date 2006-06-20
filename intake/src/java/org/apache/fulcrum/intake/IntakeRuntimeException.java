package org.apache.fulcrum.intake;

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

/**
 * Base runtime exception thrown by the Intake service.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class IntakeRuntimeException extends RuntimeException
{
    /**
     * Constructs a new <code>TurbineException</code> without specified
     * detail message.
     */
    public IntakeRuntimeException()
    {
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message.
     *
     * @param msg The error message.
     */
    public IntakeRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeRuntimeException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg    The error message.
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeRuntimeException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}