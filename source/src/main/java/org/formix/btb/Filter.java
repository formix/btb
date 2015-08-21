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

import java.util.Set;

/**
 * Filter interface used in Bridge.fill method.
 *
 * @author Jean-Philippe Gravel
 */
public interface Filter {

    /**
     * Translates this filter to its SQL query equivalent.
     *
     * @return a query that will be used afer a "WHERE" in an SQL Query.
     */
    public String toQueryString();

    /**
     * Gets the set of properties in this filter statement.
     *
     * @return a set of properties used in this filter statement.
     */
    public Set<String> getProperties();
}
