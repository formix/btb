/**
 * Copyright 2012 Jean-Philippe Gravel, P. Eng., CSDP 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.formix.btb;

/**
 * Thrown when no primary key is found for a class. Valid properties for primary
 * keys are limited to "id" and "id<i>ClassName</i>.
 *
 * @author Jean-Philippe Gravel
 */
public class PrimaryKeyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 0x6259064e8b481decL;

    /**
     * Creates the default instance of this exception.
     */
    public PrimaryKeyNotFoundException() {
        super();
    }

    /**
     * Creates an instance of the exception using the specified message.
     *
     * @param message
     *            The message of the exception.
     */
    public PrimaryKeyNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an instance of the exception using the specified cause.
     *
     * @param cause
     *            The cause of this exception.
     */
    public PrimaryKeyNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of the exception using both the specified message
     * and cause.
     *
     * @param message
     *            The message of the exception.
     * @param cause
     *            The cause of this exception.
     */
    public PrimaryKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
