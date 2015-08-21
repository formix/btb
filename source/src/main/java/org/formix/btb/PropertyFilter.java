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
import java.util.Set;

/**
 * Defines a comparison between a property and a value.
 * 
 * @see Comparators
 * @see Filter
 * @author Jean-Philippe Gravel
 */
public class PropertyFilter implements Filter {
	private Class<?> type;
	private String property;
	private Comparators comparator;
	private Object value;

	/**
	 * Creates a new instance of the class using the specified property name,
	 * comparator and value.
	 * 
	 * @param type
	 *            The type corresponding to the property (may be null)
	 * @param property
	 *            The name of the property.
	 * @param comparator
	 *            The comparator used.
	 * @param value
	 *            The value to compare with.
	 */
	public PropertyFilter(final Class<?> type, final String property,
			final Comparators comparator, final Object value) {

		Util.throwIfNullOrEmpty(property, "property");

		this.type = type;
		this.property = property;
		this.comparator = comparator;
		this.value = value;
	}

	/**
	 * Creates a new instance of the class using the specified property name,
	 * comparator and value.
	 * 
	 * @param property
	 *            The name of the property.
	 * @param comparator
	 *            The comparator used.
	 */
	public PropertyFilter(final String property, final Comparators comparator) {
		this(null, property, comparator, null);
	}

	/**
	 * Creates a new instance of the class using the specified property name,
	 * comparator and value.
	 * 
	 * @param property
	 *            The name of the property.
	 * @param comparator
	 *            The comparator used.
	 * @param value
	 *            The value to compare with.
	 */
	public PropertyFilter(final String property, final Comparators comparator,
			final Object value) {
		this(null, property, comparator, value);
	}

	/**
	 * Creates a new instance of the class using the specified property name and
	 * value. The comparator is defaulted to Comparators.EQUAL.
	 * 
	 * @param property
	 *            The name of the property.
	 * @param value
	 *            The value to compare with.
	 */
	public PropertyFilter(final String property, final Object value) {
		this(property, Comparators.EQUAL, value);
	}

	public Class<?> getType() {
		return type;
	}

	/**
	 * Gets the property name.
	 * 
	 * @return the property name.
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * Sets the property name.
	 * 
	 * @param value
	 *            the property name.
	 */
	public void setProperty(final String value) {
		this.property = value;
	}

	/**
	 * Gets the comparator.
	 * 
	 * @return the comparator.
	 */
	public Comparators getComparator() {
		return this.comparator;
	}

	/**
	 * Sets the comparator.
	 * 
	 * @param value
	 *            the comparator.
	 */
	public void setComparator(final Comparators value) {
		this.comparator = value;
	}

	/**
	 * Gets the value used for comparison.
	 * 
	 * @return the value used for comparison.
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Sets the value used for comparison.
	 * 
	 * @param value
	 *            the value used for comparison.
	 */
	public void setValue(final Object value) {
		this.value = value;
	}

	public String toQueryString() {
		String colName = property;
		if (this.type != null) {
			String typeName = this.type.getSimpleName();
			if (typeName.endsWith("Entity"))
				typeName = typeName.substring(0,typeName.length() - 6);
			else if (typeName.endsWith("Table"))
				typeName = typeName.substring(0,typeName.length() - 5);
			colName = typeName + "." + colName;
		}

		String ret = "(" + colName + " " + Util.translate(comparator);
		if ((comparator != Comparators.IS_NULL)
				&& (comparator != Comparators.IS_NOT_NULL)) {
			ret += " ?";
		}

		ret += ")";
		return ret;
	}

	public Set<String> getProperties() {
		HashSet<String> set = new HashSet<String>();
		set.add(property);
		return set;
	}

	@Override
	public String toString() {
		return toQueryString();
	}
}
