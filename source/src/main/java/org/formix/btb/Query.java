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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instances of this class are used to define a query and it's parameters.
 * 
 * @author Jean-Philippe Gravel
 */
public class Query<I> {

	private Connection connection;
	private QueryEventHandlerSet<I> creating;
	private QueryEventHandlerSet<I> created;
	private String string;
	private Class<I> assignationType;
	private Class<I> instanciationType;
	private List<Join> joins;
	private Filter filter;
	private String orderBy;
	private SqlDescriptor descriptor;
	private Map<String, String> dataColumns;
	private String primaryKey;

	/**
	 * Creates a query using the specified connection and descriptor.
	 * 
	 * @param connection
	 *            the connection to the database.
	 * 
	 * @param descriptor
	 *            the SQL descriptor of the current generic typeé
	 */
	@SuppressWarnings("unchecked")
	protected Query(Connection connection, SqlDescriptor descriptor) {
		Util.throwIfNull(descriptor, "descriptor");
		this.descriptor = descriptor;
		initialize(descriptor.getSelectQuery(), (Class<I>) descriptor.getType(), (Class<I>) descriptor.getType(),
				connection);
	}

	/**
	 * Creates a new query object using the specified string and parameters.
	 * 
	 * @param string
	 *            The select query string.
	 * @param type
	 *            The class type to be used for operations.
	 */
	public Query(String string, Class<I> type) {
		this(null, string, type, type);
	}

	/**
	 * Creates a new query object using the specified string and parameters.
	 * 
	 * @param connection
	 *            The connection to the database.
	 * @param string
	 *            The select query string.
	 * @param type
	 *            The class type to be used for operations.
	 */
	public Query(Connection connection, String string, Class<I> type) {
		this(connection, string, type, type);
	}

	/**
	 * Creates a new query object using the specified string and parameters.
	 * 
	 * @param string
	 *            The select query string.
	 * @param assignationType
	 *            The class type to be used for assignation operations.
	 * @param instanciationType
	 *            The class type to be used for instantiations operations.
	 */
	public Query(String string, Class<I> assignationType, Class<I> instanciationType) {
		Util.throwIfNullOrEmpty(string, "string");
		Util.throwIfNull(assignationType, "assignationType");
		Util.throwIfNull(instanciationType, "instanciationType");
		initialize(string, assignationType, instanciationType, null);
	}

	/**
	 * Creates a new query object using the specified string and parameters.
	 * 
	 * @param connection
	 *            The connection to the database.
	 * @param string
	 *            The select query string.
	 * @param assignationType
	 *            The class type to be used for assignation operations.
	 * @param instanciationType
	 *            The class type to be used for instantiations operations.
	 */
	public Query(Connection connection, String string, Class<I> assignationType, Class<I> instanciationType) {
		Util.throwIfNullOrEmpty(string, "string");
		Util.throwIfNull(assignationType, "assignationType");
		Util.throwIfNull(instanciationType, "instanciationType");
		initialize(string, assignationType, instanciationType, connection);
	}

	private void initialize(String string, Class<I> assignationType, Class<I> instanciationType,
			Connection connection) {
		Util.throwIfNullOrEmpty(string, "string");
		Util.throwIfNull(assignationType, "assignationType");
		Util.throwIfNull(instanciationType, "instanciationType");
		this.string = string;
		this.assignationType = assignationType;
		this.instanciationType = instanciationType;
		filter = null;
		orderBy = null;
		if (descriptor == null) {
			descriptor = SqlDescriptor.getInstance(assignationType);
		}
		this.joins = new ArrayList<Join>();
		created = new QueryEventHandlerSet<I>();
		creating = new QueryEventHandlerSet<I>();
		this.dataColumns = null;
		this.primaryKey = null;
		this.connection = connection;
	}

	/**
	 * Adds a post initialization handler.
	 * 
	 * @param handler
	 *            the handler to add.
	 */
	public void addCreatedHandler(QueryEventHandler<I> handler) {
		Util.throwIfNull(handler, "handler");
		created.add(handler);
	}

	/**
	 * Removes a post initialization handler.
	 * 
	 * @param handler
	 *            the handler to remove.
	 */
	public void removeCreatedHandler(QueryEventHandler<I> handler) {
		Util.throwIfNull(handler, "handler");
		created.remove(handler);
	}

	/**
	 * @return the postInitializeHandler set.
	 */
	public QueryEventHandlerSet<I> getCreatedHandler() {
		return created;
	}

	/**
	 * Adds a creating handler. This handle is called before the initialization
	 * of the object.
	 * 
	 * @param handler
	 *            the handler to add.
	 */
	public void addCreatingHandler(QueryEventHandler<I> handler) {
		Util.throwIfNull(handler, "handler");
		creating.add(handler);
	}

	/**
	 * Removes a creating handler.
	 * 
	 * @param handler
	 *            the handler to remove.
	 */
	public void removeCreatingHandler(QueryEventHandler<I> handler) {
		Util.throwIfNull(handler, "handler");
		creating.remove(handler);
	}

	/**
	 * @return the CreatingHandler set.
	 */
	public QueryEventHandlerSet<I> getCreatingHandler() {
		return creating;
	}

	/**
	 * Gets the primary key for the current query.
	 * 
	 * @return the query primary key, if it exists or null.
	 */
	public String getPrimaryKey() {
		if (primaryKey == null) {
			return descriptor.getPrimaryKey();
		}
		return primaryKey;
	}

	/**
	 * Sets the primary key for the current query. Setting a null value will
	 * reset the primaryKey value to the default value set from the bridge.
	 * 
	 * @param value
	 *            the primary key property name.
	 */
	public void setPrimaryKey(String value) {
		this.primaryKey = value;
	}

	/**
	 * Gets the type that will used to set values to the class instance during
	 * operations.
	 * 
	 * @return a Class object representing the type that will be used to set
	 *         values to the class instance during operations.
	 */
	public Class<I> getAssignationType() {
		return assignationType;
	}

	/**
	 * Gets the type that will be created during fill operations.
	 * 
	 * @return a Class object representing the type that will be created during
	 *         fill operations.
	 */
	public Class<I> getInstanciationType() {
		return instanciationType;
	}

	/**
	 * @return the joins list associated to this query.
	 */
	public List<Join> getJoins() {
		return joins;
	}

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Sets the filter.
	 * 
	 * @param filter
	 *            A filter object.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Gets the filter.
	 * 
	 * @return the filter object.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Sets the order by clause.
	 * 
	 * @param orderBy
	 *            The orderBy clause.
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Gets the orderBy clause.
	 * 
	 * @return the order by clause.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * Counts the number of rows that will be returned by the current query.
	 * Execute a SELECT COUNT on the underlying table using the current filters.
	 * 
	 * @return The number of rows that will be returned by the executeFill
	 *         methods.
	 * @throws SQLException
	 *             if an error occurs during the SELECT COUNT operation.
	 */
	public int count() throws SQLException {

		// String beginsWith = "SELECT COUNT(*) FROM (" + this.toString() +
		// ")q";
		String strQuery = "SELECT COUNT(*) FROM (" + this.toString() + ")q";

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		PreparedStatement stmt = connection.prepareStatement(strQuery);
		setParameters(stmt, filter);
		ResultSet rs = stmt.executeQuery();

		int count = 0;
		if (rs.next()) {
			count = rs.getInt(1);
		}

		stmt.close();

		if (closeConn) {
			Connector.closeConnection();
		}

		return count;
	}

	/**
	 * Execute the query and returns the resulting object list.
	 * 
	 * @return the list containing the result of the query.
	 * 
	 * @throws SQLException
	 *             if an error occurs during the query execution.
	 */
	public List<I> execute() throws SQLException {
		ArrayList<I> list = new ArrayList<I>();
		executeFill(list);
		return list;
	}

	/**
	 * Executes the query and fills the specified list. If the collection isn't
	 * empty, contained elements will be updated.
	 * 
	 * @param col
	 *            the collection to be filled.
	 * 
	 * @throws SQLException
	 *             if an error occurs during the query execution.
	 */
	public void executeFill(Collection<? super I> col) throws SQLException {
		Util.throwIfNull(col, "col");
		String strQuery = buildSelectQuery(string, filter, orderBy);

		Connection connection = this.connection;
		boolean closeConn = false;
		if (connection == null) {
			closeConn = true;
			connection = Connector.openConnection();
		}

		PreparedStatement stmt = connection.prepareStatement(strQuery);
		setParameters(stmt, filter);
		try {
			ResultSet rs = stmt.executeQuery();
			if (this.dataColumns == null)
				this.createDataColumns(rs.getMetaData());
			fillCollection(col, rs);
			rs.close();
		} catch (SQLException e) {
			throw new SQLException("SQL Statement error: " + strQuery, e);
		} finally {
			if (closeConn) {
				Connector.closeConnection();
			}
			stmt.close();
		}
	}

	private void createDataColumns(ResultSetMetaData metaData) throws SQLException {
		this.dataColumns = new HashMap<String, String>();
		for (int col = 1; col <= metaData.getColumnCount(); col++) {
			String colLabel = metaData.getColumnLabel(col);
			this.dataColumns.put(colLabel.toLowerCase(), colLabel);
		}
	}

	private String buildSelectQuery(String beginsWith, Filter filter, String orderBy) {
		String query = beginsWith;
		if (this.joins.size() > 0) {
			for (Join join : this.joins) {
				query += " " + join;
			}
		}
		if (filter != null) {
			query += " WHERE " + filter;
		}
		if (orderBy != null) {
			query += " ORDER BY " + orderBy;
		}
		return query;
	}

	private void setParameters(PreparedStatement stmt, Filter filter) throws SQLException {

		if (filter == null) {
			return;
		}

		setParameters(stmt, filter, 1);
	}

	private int setParameters(PreparedStatement stmt, Filter filter, int parameterIndex) throws SQLException {

		Util.throwIfNull(stmt, "stmt");
		Util.throwIfNull(filter, "filter");

		int index = parameterIndex;

		if (filter instanceof PropertyFilter) {
			PropertyFilter colFilter = (PropertyFilter) filter;
			if (colFilter.getValue() != null) {

				// Convert calendar value to Timestamp
				Object value = colFilter.getValue();
				if (value instanceof Calendar) {
					Calendar cal = (Calendar) value;
					value = new Timestamp(cal.getTimeInMillis());
				}

				stmt.setObject(index, value);
				return index + 1;
			}
		} else if (filter instanceof LogicalFilter) {
			LogicalFilter logicalFilter = (LogicalFilter) filter;
			for (Filter f : logicalFilter.getFilters()) {
				index = setParameters(stmt, f, index);
			}
		}

		return index;
	}

	@SuppressWarnings("unchecked")
	private void fillCollection(Collection<? super I> col, ResultSet rs) throws SQLException {

		Map<Object, Object> map = createMap(col);

		while (rs.next()) {

			Object key = null;
			String primarykey = getPrimaryKey();
			if (primarykey != null) {
				key = rs.getObject(primarykey);
			}

			I item = null;
			if (map.containsKey(key)) {
				item = (I) map.get(key);
			} else {
				try {
					item = createInstance(key);
				} catch (Exception ex) {
					throw new UnexpectedException(
							"Unable to create an " + "object of type " + this.instanciationType.getName(), ex);
				}
			}

			if (item != null) {
				setValues(item, rs);
				QueryEvent<I> action = new QueryEvent<I>(this, item);
				onCreated(action);
				if (!action.isCancelled() && !map.containsKey(key))
					col.add(item);
			}

		}
		rs.close();
	}

	private Map<Object, Object> createMap(Collection<? super I> col) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (Object item : col) {
			Object key = descriptor.getValue(item, getPrimaryKey());
			map.put(key, item);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private <E extends I> E createInstance(Object itemId)
			throws IllegalAccessException, InstantiationException, InvocationTargetException {
		QueryEvent<E> action = new QueryEvent<E>(this, null, itemId);
		onCreating(action);
		if (action.isCancelled()) {
			return null;
		}
		E newInstance = action.getItem();
		if (newInstance == null) {
			newInstance = (E) instanciationType.newInstance();
			if (itemId != null) {
				this.descriptor.setValue(newInstance, getPrimaryKey(), itemId);
			}
		}
		return newInstance;
	}

	private void setValues(I item, ResultSet rs) throws SQLException {
		for (String column : descriptor.getColumns()) {
			try {
				String lcColumn = column.toLowerCase();
				if (!column.equals(getPrimaryKey()) && this.dataColumns.containsKey(lcColumn)) {
					String dbColumn = this.dataColumns.get(lcColumn);
					descriptor.setValue(item, column, rs.getObject(dbColumn));
				}
			} catch (InvocationTargetException ex) {
				throw new UnexpectedException(
						"Unable to set [" + item + "] in [" + column + "]. Please see inner exceptions.", ex);
			} catch (IllegalAccessException ex) {
				throw new UnexpectedException(
						"Unable to set [" + item + "] in [" + column + "]. Please see inner exceptions.", ex);
			}
		}
	}

	/**
	 * This method is called before the creation of a new object. This event
	 * gives the occasion to the user to take control of the object creation.
	 * Using this event, it's possible to seek a cache for example. Override to
	 * control the event propagation.
	 * 
	 * @param <E>
	 *            The type of the object to be created.
	 * 
	 * @param action
	 *            The action containing the event data.
	 */
	protected <E extends I> void onCreating(QueryEvent<E> action) {
		Util.throwIfNull(action, "action");
		creating.handle(action);
	}

	/**
	 * This method is called each time an object has been initialized with the
	 * current row data. Override to control the event propagation.
	 * 
	 * @param action
	 *            The action containing the event data.
	 */
	protected void onCreated(QueryEvent<I> action) {
		Util.throwIfNull(action, "action");
		created.handle(action);
	}

	@Override
	public String toString() {
		return buildSelectQuery(string, filter, orderBy);
	}
}
