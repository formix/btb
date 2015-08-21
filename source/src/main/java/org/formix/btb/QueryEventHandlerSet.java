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

import java.util.HashSet;

/**
 * A set used to contain and call QueryEventHandlers. Note that the call order
 * will not fit with the handler addition.
 * 
 * @author Jean-Philippe Gravel
 * @param <T>
 *            The type of the item handled.
 */
public class QueryEventHandlerSet<T> extends HashSet<QueryEventHandler<T>>
        implements QueryEventHandler<T> {
    
    private static final long serialVersionUID = 0x90FB56BB5379A47FL;
    
    public <E extends T> void handle(QueryEvent<E> action) {
        for (QueryEventHandler<T> handler : this) {
            handler.handle(action);
        }
    }
}
