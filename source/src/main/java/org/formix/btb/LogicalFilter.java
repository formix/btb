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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a logical operation between a list of other filter objects. Contains
 *
 * @see Operators
 * @see Filter
 * @author Jean-Philippe Gravel
 */
public class LogicalFilter implements Filter {

    private ArrayList<Filter> filters;
    private Operators         operator;

    /**
     * Creates a new logical filter.
     *
     * @param operator
     *            The operator used to join inner filters.
     * @param filters
     *            The filters to be joined.
     */
    public LogicalFilter(Operators operator, Filter... filters) {
        this.filters = this.createArrayList(filters);
        this.operator = operator;
    }

    private ArrayList<Filter> createArrayList(Filter[] filters) {
        ArrayList<Filter> ret = new ArrayList<Filter>();
        for (Filter filter : filters) {
            ret.add(filter);
        }
        return ret;
    }

    /**
     * Gets the operator used in this filter.
     *
     * @return the used logical operator.
     */
    public Operators getOperator() {
        return this.operator;
    }

    /**
     * Sets the currently used logical operator.
     *
     * @param value
     *            the value of the operator.
     */
    public void setOperator(Operators value) {
        this.operator = value;
    }

    /**
     * Gets the inner filter list.
     *
     * @return the inner filter list.
     */
    public ArrayList<Filter> getFilters() {
        return this.filters;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.btb.Filter#toQueryString()
     */
    public String toQueryString() {
        String ret = "";

        if (this.operator != Operators.NOT) {
            int i = 0;
            ret = "(";

            for (Filter filter : this.filters) {
                if (i > 0) {
                    ret += " " + Util.translate(this.operator) + " ";
                }
                ret += filter.toQueryString();
                i++;
            }

            ret += ")";
        } else {
            ret = Util.translate(this.operator) + " "
                    + this.filters.get(0).toQueryString();
        }

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.btb.Filter#getProperties()
     */
    public Set<String> getProperties() {
        HashSet<String> set = new HashSet<String>();
        for (Filter f : filters) {
            set.addAll(f.getProperties());
        }
        return set;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.toQueryString();
    }
}
