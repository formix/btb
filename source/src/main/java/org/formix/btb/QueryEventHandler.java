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
 * Interface used to handle Query events.
 * 
 * @author Jean-Philippe Gravel
 * @param <T>
 *            The type of the object on witch the query is operating on.
 */
public interface QueryEventHandler<T> {
    /**
     * Handles a Query event.
     * 
     * @param qe
     *            The query event informations.
     */
    public <E extends T> void handle(QueryEvent<E> qe);
}
