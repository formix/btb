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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SqlDescriptor {

	private static HashMap<Class<?>, SqlDescriptor> cache;

	public static SqlDescriptor getInstance(Class<?> type,
			String... ignoredProperties) throws PrimaryKeyNotFoundException {

		Set<Object> key = new HashSet<Object>();
		key.add(type);
		Collections.addAll(key, ignoredProperties);

		// If the current type is found in the cache, we return it.
		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		return new SqlDescriptor(type, ignoredProperties);
	}

	static {
		cache = new HashMap<Class<?>, SqlDescriptor>();
	}

	private Class<?> type;
	private HashMap<String, Method> accessors;
	private HashMap<String, Method> mutators;
	private List<String> columns;
	private Set<String> readOnlyColumns;
	private Set<String> ignoredProperties;
	private String tableName;
	private String primaryKey;
	private String selectQuery;
	private String insertQuery;
	private String updateQuery;
	private String deleteQuery;

	private SqlDescriptor(Class<?> type, String[] ignoredProperties)
			throws PrimaryKeyNotFoundException {
		Util.throwIfNull(type, "type");
		this.type = type;
		this.ignoredProperties = new HashSet<String>();
		Collections.addAll(this.ignoredProperties, ignoredProperties);
		initialize();
	}

	private void initialize() {
		tableName = findTableName(this.type);
		accessors = getColumnMethodMap("get", this.type);
		accessors.putAll(getColumnMethodMap("is", this.type));
		mutators = getColumnMethodMap("set", this.type);
		primaryKey = guessPrimayKey(tableName);
		columns = createColumnList(accessors, mutators);
	}

	public void initializeQueries(Set<String> roCols) {
		Util.throwIfNull(roCols, "roCols");
		this.readOnlyColumns = translateColumns(roCols);
		selectQuery = createSelectQuery();
		insertQuery = createInsertQuery(readOnlyColumns);
		updateQuery = createUpdateQuery(readOnlyColumns);
		deleteQuery = createDeleteQuery();
	}

	private Set<String> translateColumns(Set<String> readOnlyColumns) {
		HashMap<String, String> allCols = new HashMap<String, String>();
		for (String column : columns) {
			allCols.put(column.toLowerCase(), column);
		}

		HashSet<String> roCols = new HashSet<String>();
		for (String column : readOnlyColumns) {
			String lcCol = column.toLowerCase();
			if (allCols.containsKey(lcCol)) {
				roCols.add(allCols.get(lcCol));
			}
		}

		return roCols;
	}

	public Set<String> getIgnoredProperties() {
		return Collections.unmodifiableSet(this.ignoredProperties);
	}

	private List<String> createColumnList(HashMap<String, Method> accessors,
			HashMap<String, Method> mutators) {
		TreeSet<String> columns = new TreeSet<String>(accessors.keySet());
		columns.addAll(mutators.keySet());
		return new ArrayList<String>(columns);
	}

	public Class<?> getType() {
		return type;
	}

	public String getTableName() {
		return tableName;
	}

	private String findTableName(Class<?> type) {
		String tableName = type.getSimpleName();
		if (tableName.endsWith("Entity")) {
			tableName = tableName.substring(0, tableName.length() - 6);
		} else if (tableName.endsWith("Table")) {
			tableName = tableName.substring(0, tableName.length() - 5);
		}
		return tableName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	private String guessPrimayKey(String tableName) {
		String key = null;
		String[] guesses = new String[] { "id", tableName + "Id" };

		for (String guess : guesses) {
			if (accessors.containsKey(guess)) {
				key = guess;
				break;
			}
		}

		return key;
	}

	private String decapitalize(String value) {
		return value.substring(0, 1).toLowerCase()
				+ value.substring(1, value.length());
	}

	private HashMap<String, Method> getColumnMethodMap(String prefix,
			Class<?> type) {

		int paramCount = 0;
		if (prefix.equals("set"))
			paramCount = 1;

		HashMap<String, Method> methodMap = new HashMap<String, Method>();

		Method[] methods = type.getDeclaredMethods();
		for (Method method : methods) {

			// Methods with more than one parameter are not covered.
			if (method.getParameterTypes().length > 1)
				continue;

			// Collection setters are forbidden
			if (method.getParameterTypes().length == 1) {
				Class<?> paramType = method.getParameterTypes()[0];
				if (Collection.class.isAssignableFrom(paramType))
					continue;
			}

			// Collection getters are forbidden too.
			Class<?> retType = method.getReturnType();
			if (retType != null) {
				if (Collection.class.isAssignableFrom(retType))
					continue;
			}

			int prefixLength = prefix.length();

			String methodName = method.getName();
			String propertyName = methodName.substring(prefixLength);
			if (this.ignoredProperties.contains(propertyName.toLowerCase())) {
				continue;
			}

			if ((methodName.startsWith(prefix))
					&& (method.getParameterTypes().length == paramCount)) {
				String colName = propertyName;
				colName = decapitalize(colName);
				method.setAccessible(true);
				methodMap.put(colName, method);
			}
		}

		// Obtains get/set methods from supertypes.
		Set<Class<?>> parentClassList = getInheritance(type);
		for (Class<?> parentClass : parentClassList) {
			if ((parentClass != Object.class) && (parentClass != null)) {
				HashMap<String, Method> parentMethods = getColumnMethodMap(
						prefix, parentClass);
				methodMap.putAll(parentMethods);
			}
		}

		return methodMap;
	}

	public List<String> getColumns() {
		return columns;
	}

	public Set<String> getReadOnlyColumns() {
		return readOnlyColumns;
	}

	public String getColumn(int index) {
		return columns.get(index);
	}

	public String getSelectQuery() {
		return selectQuery;
	}

	private String createSelectQuery() {

		String query = "SELECT ";
		int i = 0;

		for (String column : getColumns()) {
			if (this.ignoredProperties.contains(column)) {
				continue;
			}

			if (i > 0) {
				query += ", ";
			}
			query += this.getTableName() + "." + column;
			i++;
		}

		query += " FROM " + getTableName();

		return query;
	}

	public String getInsertQuery() {
		return insertQuery;
	}

	private String createInsertQuery(Set<String> readOnlyColumns) {

		String query = "INSERT INTO " + getTableName() + " ";
		String cols = "(";
		String values = "(";
		boolean first = true;

		for (String column : columns) {
			if (readOnlyColumns.contains(column)
					|| this.ignoredProperties.contains(column)) {
				continue;
			}
			if (!first) {
				cols += ", ";
				values += ", ";
			}
			cols += column;
			values += "?";
			first = false;

		}

		values += ")";
		cols += ")";
		query += cols + " VALUES " + values;

		return query;
	}

	public String getUpdateQuery() {
		return updateQuery;
	}

	private String createUpdateQuery(Set<String> readOnlyColumns) {
		String query = "UPDATE " + getTableName() + " SET ";
		boolean first = true;

		for (String column : columns) {

			if (readOnlyColumns.contains(column)
					|| this.ignoredProperties.contains(column)) {
				continue;
			}

			if (!first) {
				query += ", ";
			}

			query += column + " = ?";
			first = false;
		}

		return query + " WHERE " + primaryKey + " = ?";
	}

	public String getDeleteQuery() {
		return deleteQuery;
	}

	private String createDeleteQuery() {

		String query = "DELETE FROM " + getTableName() + " WHERE " + primaryKey
				+ " = ?";
		return query;
	}

	protected void setValue(Object item, String property, Object value)
			throws InvocationTargetException, IllegalAccessException {

		Util.throwIfNull(item, "item");
		Util.throwIfNullOrEmpty(property, "property");

		Method m = mutators.get(property);

		// Code to support calendar properties.
		Object valueToAssign = value;
		if (valueToAssign instanceof Date) {
			Date date = (Date) valueToAssign;
			Class<?> paramType = m.getParameterTypes()[0];
			if (Calendar.class.isAssignableFrom(paramType)) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(date.getTime());
				valueToAssign = cal;
			}
		}

		try {
			m.invoke(item, valueToAssign);
		} catch (Exception e) {
			Class<?> paramType = m.getParameterTypes()[0];
			String msg = e.getMessage() + " : " + "Class: "
					+ item.getClass().getName() + ", Method: " + m.getName()
					+ "(" + paramType.getName() + "), Received: " + value
					+ " (" + value.getClass().getName() + ").";
			throw new IllegalArgumentException(msg);
		}
	}

	protected Object getValue(Object item, String property) {

		Util.throwIfNull(item, "item");
		Util.throwIfNullOrEmpty(property, "property");

		Method m = accessors.get(property);
		if (m == null) {
			throw new IllegalStateException(String.format(
					"There is no accessor for the property [%s.%s].", this
							.getType().getName(), property));
		}

		Object value = null;
		try {
			value = m.invoke(item, new Object[] {});
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return value;
	}

	public boolean isMutable(String property) {
		return mutators.containsKey(property);
	}

	public boolean isAccessible(String property) {
		return accessors.containsKey(property);
	}

	public boolean isReadOnly(String column) {
		return readOnlyColumns.contains(column);
	}

	@Override
	public String toString() {
		String ret = "Table       : " + getTableName() + "\n";
		ret += "Primary key : " + getPrimaryKey() + "\n";
		ret += "Columns     : " + new TreeSet<String>(getColumns()) + "\n";
		ret += "Select      : " + getSelectQuery() + "\n";
		ret += "Insert      : " + getInsertQuery() + "\n";
		ret += "Update      : " + getUpdateQuery() + "\n";
		ret += "Delete      : " + getDeleteQuery() + "\n";
		return ret;
	}

	public static Set<Class<?>> getInheritance(Class<?> in) {
		LinkedHashSet<Class<?>> result = new LinkedHashSet<Class<?>>();

		// result.add(in);
		getInheritance(in, result);

		return result;
	}

	/**
	 * Get inheritance of type.
	 * 
	 * @param in
	 * @param result
	 */
	private static void getInheritance(Class<?> in, Set<Class<?>> result) {
		Class<?> superclass = getSuperclass(in);

		if (superclass != null) {
			result.add(superclass);
			getInheritance(superclass, result);
		}

		getInterfaceInheritance(in, result);
	}

	/**
	 * Get interfaces that the type inherits from.
	 * 
	 * @param in
	 * @param result
	 */
	private static void getInterfaceInheritance(Class<?> in,
			Set<Class<?>> result) {
		for (Class<?> c : in.getInterfaces()) {
			result.add(c);

			getInterfaceInheritance(c, result);
		}
	}

	/**
	 * Get superclass of class.
	 * 
	 * @param in
	 *            the class to get the superclass from.
	 *            
	 * @return the superclass of the "in" class.
	 */
	private static Class<?> getSuperclass(Class<?> in) {
		if (in == null) {
			return null;
		}

		if (in.isArray() && in != Object[].class) {
			Class<?> type = in.getComponentType();

			while (type.isArray()) {
				type = type.getComponentType();
			}

			return type;
		}

		return in.getSuperclass();
	}
}
