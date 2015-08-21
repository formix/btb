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
 * Used to compare a property with a database column value in PropertyFilter
 * objects.
 *
 * @author Jean-Philippe Gravel
 */
public enum Comparators {
    /**
     * Unary comparator, check if the property is null.
     */
    IS_NULL,
    /**
     * Unary comparator, check if the property is not null.
     */
    IS_NOT_NULL,
    /**
     * Check if the property is lower than the specified value.
     */
    LOWER,
    /**
     * Check if the property is lower or equal to the specified value.
     */
    LOWER_OR_EQUAL,
    /**
     * Check if the property is equal to the specified value.
     */
    EQUAL,
    /**
     * Check if the property is different to the specified value.
     */
    DIFFERENT,
    /**
     * Check if the property is like the given string value, using wildcards.
     */
    LIKE,
    /**
     * Check if the property is not like the given string value, using
     * wildcards.
     */
    NOT_LIKE,
    /**
     * Check if the property is grater than the specified value.
     */
    GREATER,
    /**
     * Check if the property is grater or equal to the specified value.
     */
    GREATER_OR_EQUAL
}
