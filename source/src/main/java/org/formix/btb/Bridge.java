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

import java.sql.Statement;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Defines a data bridge that is responsible for object persistence to a
 * database. This object relational mapper is optimised to work with POJOs
 * respecting these behaviors:
 * </p>
 * <p>
 * <ol>
 * <li>The class must provide a public default (parameterless) constructor (but
 * you can define any constructor you wish thereafter).</li>
 * <li>The class must provide both read and write methods for each of its
 * properties (but they don't have to be public).</li>
 * </ol>
 * </p>
 * <p>
 * When an object is passed to any of the Bridge's insert, update, delete of
 * fill method, the object is assumed to be a persistable entity. The name of
 * the class is assumed to be the name of a table and the object's properties
 * are assumed to be the table's columns.
 * </p>
 * <p>
 * <u>Note for case sensitive RDBMS</u><br>
 * Do not forget to match the capitalization from your class names to your table
 * names. Further more properties are camelcased. For example, take the
 * hypotetic property "first name" (getFirstName() and setFirstName() methods).
 * This property is assumed to match the column name "firstName".
 * </p>
 * 
 * @author Jean-Philippe Gravel
 */
public class Bridge<A> {

	private Class<A> type;
	private Connection connection;
	private SqlDescriptor descriptor;

	/**
	 * Shortcut method used to create a Bridge.
	 * 
	 * @param <A>
	 *            The type of the corresponding object to create. Automatically
	 *            inferred from the type parameter.
	 * 
	 * @param type
	 *            The type of the class to be bridged.
	 * 
	 * @return A Bridge instance.
	 * 
	 * @throws SQLException
	 */
	public static <A> Bridge<A> create(Class<A> type,
			String... ignoredProperties) throws SQLException {
		return new Bridge<A>(null, type, ignoredProperties);
	}

	/**
	 * Shortcut method used to create a Bridge.
	 * 
	 * @param <A>
	 *            The type of the corresponding object to create. Automatically
	 *            inferred from the type parameter.
	 * 
	 * @param connection
	 *            The connection to the database.
	 * 
	 * @param type
	 *            The type of the class to be bridged.
	 * 
	 * @return A Bridge instance.
	 * 
	 * @throws SQLException
	 */
	public static <A> Bridge<A> create(Connection connection, Class<A> type,
			String... ignoredProperties) throws SQLException {
		return new Bridge<A>(connection, type, ignoredProperties);
	}

	/**
	 * Creates a new Bridge using the default connection string as defined in
	 * the "bridge.xml" file. See net.sf.btb.ConnectionManager for more
	 * informations about this file.
	 * 
	 * @param type
	 *            The type of the class to be bridged.
	 * @see ConnectionManager
	 */
	public Bridge(Class<A> type, String... ignoredProperties)
			throws SQLException {
		Util.throwIfNull(type, "type");
		initialize(null, type, ignoredProperties);
	}

	/**
	 * Creates a new Bridge using the default connection string as defined in
	 * the "bridge.xml" file. See net.sf.btb.ConnectionManager for more
	 * informations about this file.
	 * 
	 * @param connection
	 *            The connection to the database.
	 * @param type
	 *            The type of the class to be bridged.
	 * @see ConnectionManager
	 */
	public Bridge(Connection connection, Class<A> type,
			String... ignoredProperties) throws SQLException {
		Util.throwIfNull(type, "type");
		initialize(connection, type, ignoredProperties);
	}

	private void initialize(Connection connection, Class<A> type,
			String[] ignoredProperties) throws SQLException {
		this.connection = connection;
		descriptor = SqlDescriptor.getInstance(type, ignoredProperties);
		this.type = type;
		if (descriptor.getReadOnlyColumns() == null) {
			Set<String> roCols = getReadOnlyColumns(descriptor.getTableName());
			descriptor.initializeQueries(roCols);
		}
	}

	private Set<String> getReadOnlyColumns(String tableName)
			throws SQLException {

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		String query = "SELECT * FROM " + tableName;
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSetMetaData meta = stmt.getMetaData();

		HashSet<String> roCols = new HashSet<String>();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			if (meta.isReadOnly(i) || meta.isAutoIncrement(i)) {
				roCols.add(meta.getColumnName(i));
			}
		}

		stmt.close();

		if (closeConn) {
			Connector.closeConnection();
		}

		return roCols;
	}

	/**
	 * Gets the type bridged by the current object.
	 * 
	 * @return the type bridged.
	 */
	public Class<A> getType() {
		return type;
	}

	/**
	 * Gets the internal SqlDescriptor.
	 * 
	 * @return
	 */
	protected SqlDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * @return an unmodifiable set containing all ignored properties by the
	 *         current bridge.
	 */
	public Set<String> getIgnoredProperties() {
		return this.descriptor.getIgnoredProperties();
	}

	/**
	 * @return the select query.
	 */
	protected String getSelectQuery() {
		return this.descriptor.getSelectQuery();
	}

	/**
	 * Gets the actual bridge connection. Null if no connection have been passed
	 * to the bridge.
	 * 
	 * @return null or a connection, depending on the constructor used.
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Sets the current connection. If the bridge should use the default
	 * connection, set it to null.
	 * 
	 * @deprecated This method does nothing anymore. The bridge connection
	 *             cannot be changed at runtime. «Use the singleton instance
	 *             from the Connector class or pass a connection object to the
	 *             bridge constructor.
	 * 
	 * @param connection
	 *            The connection to use to get data.
	 */
	public void setConnection(Connection connection) {
	}

	/**
	 * Creates a query to select objects of the current generic type.
	 * 
	 * @return a new query object.
	 */
	public Query<A> newQuery() {
		return newQuery(getType());
	}

	/**
	 * Create a query to select object of the current generic type into a
	 * derived class type.
	 * 
	 * @param desiredType
	 *            The derived class type to be used by the created query.
	 * 
	 * @return a Query object that will generate objects from the specified
	 *         desired type.
	 */
	public <I extends A> Query<I> newQuery(Class<I> desiredType) {
		Util.throwIfNull(desiredType, "desiredType");
		Query<I> q = new Query<I>(connection, descriptor);
		return q;
	}

	private void setStatementValue(PreparedStatement stmt, int parameterIndex,
			Object value) throws SQLException {
		if (value != null) {
			if (value instanceof Character) {
				stmt.setObject(parameterIndex, value, Types.CHAR);
			} else {
				stmt.setObject(parameterIndex, value);
			}
		} else {
			stmt.setNull(parameterIndex, Types.VARCHAR);
		}
	}

	/**
	 * Insert the specified item in the database.
	 * 
	 * @param item
	 *            The item to insert.
	 * @throws SQLException
	 *             if a problem occurs during database operations.
	 */
	public void insert(A item) throws SQLException {
		Util.throwIfNull(item, "item");

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		PreparedStatement stmt = connection.prepareStatement(
				descriptor.getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
		setParameters(stmt, item);
		stmt.executeUpdate();
		fetchBackAutoKey(item, stmt.getGeneratedKeys());
		stmt.close();

		if (closeConn) {
			Connector.closeConnection();
		}
	}

	private int setParameters(PreparedStatement stmt, Object item)
			throws SQLException {

		int parameterIndex = 1;
		for (String columnName : descriptor.getColumns()) {

			if (descriptor.getReadOnlyColumns().contains(columnName)
					|| this.descriptor.getIgnoredProperties().contains(
							columnName)) {
				continue;
			}

			// Added to support Calendar properties.
			Object value = descriptor.getValue(item, columnName);
			if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = new Timestamp(cal.getTimeInMillis());
			}

			setStatementValue(stmt, parameterIndex, value);
			parameterIndex++;

		}

		return parameterIndex;
	}

	// Supports only one auto generated key.
	private void fetchBackAutoKey(Object item, ResultSet rs)
			throws SQLException {
		if (rs.next() && (rs.getObject(1) != null)) {
			String key = descriptor.getPrimaryKey();
			Object value = rs.getObject(1);

			if (value instanceof BigDecimal) {
				BigDecimal castVal = (BigDecimal) value;
				value = castVal.intValue();
			}
			setItemValue(item, key, value);
		}

		rs.close();
	}

	private void setItemValue(Object item, String columnName, Object value) {
		try {
			descriptor.setValue(item, columnName, value);
		} catch (Exception ex) {
			throw new UnexpectedException("Unable to set [" + value + "] to "
					+ "the property [" + columnName + "] for the type + "
					+ item.getClass().getName() + ".", ex);
		}
	}

	/**
	 * Updates the specified item in the database.
	 * 
	 * @param item
	 *            The item to update.
	 * @throws SQLException
	 *             if a problem occurs during database operations.
	 */
	public void update(A item) throws SQLException {
		Util.throwIfNull(item, "item");

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		PreparedStatement stmt = connection.prepareStatement(descriptor
				.getUpdateQuery());
		int parameterIndex = setParameters(stmt, item);
		setStatementKey(stmt, parameterIndex, item);
		stmt.executeUpdate();
		stmt.close();

		if (closeConn) {
			Connector.closeConnection();
		}
	}

	private void setStatementKey(PreparedStatement stmt, int parameterIndex,
			Object item) throws SQLException {
		Object value = descriptor.getValue(item, descriptor.getPrimaryKey());
		setStatementValue(stmt, parameterIndex, value);
		parameterIndex++;
	}

	/**
	 * Deletes the specified item from the database.
	 * 
	 * @param item
	 *            The item to delete.
	 * @throws SQLException
	 *             if a problem occurs during database operations.
	 */
	public void delete(A item) throws SQLException {
		Util.throwIfNull(item, "item");

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		PreparedStatement stmt = connection.prepareStatement(descriptor
				.getDeleteQuery());
		setStatementKey(stmt, 1, item);
		stmt.executeUpdate();
		stmt.close();

		if (closeConn) {
			Connector.closeConnection();
		}
	}

}
