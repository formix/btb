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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.formix.btb.ConnectionManager;

/**
 * 
 * <p>
 * Manage a single connection for all bridge instance and any other object
 * needing a connection to a database. The Connector.open() method creates a 
 * connection with autoCommit set to false. Upon further calls to open a 
 * connection, instead of creating a new connection, it increments a stack 
 * counter and returns the connection already created. Upon close, the stack 
 * counter is decreased and when it reaches 0, commit the transaction, close 
 * the connection and set it to null. If a roll back is called, the Connector 
 * rolls back the transaction, close and set the connection to null and reset 
 * the stack counter to 0. Each connections created are bound to the thread 
 * it has been created into. So calls to Connector.open() and 
 * Connector.close() must match inside a thread otherwise the thread 
 * transaction will remain pending. 
 * </p>
 * <p>
 * <ul>
 * <li><b>database.url=<i>connection string</i></b> Defines the default
 * connection string. The attribute is required for the connection manager to
 * work properly.</li>
 * <li><b>database.url.{name}=<i>connection string</i></b> It's possible to
 * define as many connection strings as you want provided that you specify a
 * different <i>name</i> for each.</li>
 * <li><b>database.default=<i>{name}</i></b> If you want another connection
 * string to be used by default, specify it's name with this attribute. Note
 * that the <i>database.url.</i> part is omitted.</li>
 * </ul>
 * </p>
 * <p>
 * You can change the path of the database.properties file by setting the system
 * property attribute "databases.properties.path" to the desired file path.
 * </p>
 * 
 * @author Jean-Philippe Gravel
 * 
 */
/**
 * @author jpgravel
 * 
 */
public class Connector {

	private static final String DISABLED_ERROR_STRING = "This operation "
			+ "failed because the connector is disabled."
			+ "Call Connector.enable() to be able to use automatic"
			+ "connection.";

	private static final Integer STATIC_SYNC = new Integer(0);
	private static Map<Long, Connector> instances = null;

	/**
	 * The singleton connector based on the ConnectionManager using the default
	 * connection string.
	 * 
	 * @return The newly created connector.
	 */
	public static Connector getInstance() {
		synchronized (STATIC_SYNC) {
			if (instances == null) {
				instances = Collections
						.synchronizedMap(new HashMap<Long, Connector>());
			}
			Long threadId = Thread.currentThread().getId();
			if (!instances.containsKey(threadId)) {
				instances.put(threadId, new Connector());
			}
			return instances.get(threadId);
		}
	}

	/**
	 * Open a connection to the default database.
	 * 
	 * @return The connection on the default database.
	 * 
	 * @throws SQLException
	 *             Thrown when a problem occurs during SQL execution.
	 */
	public static Connection openConnection() throws SQLException {
		return getInstance().open();
	}

	/**
	 * Rollback all transactions based on the current connection.
	 */
	public static void rollBack() {
		getInstance().rollback();
	}

	/**
	 * Close the current connection.
	 */
	public static void closeConnection() {
		getInstance().close();
	}

	/**
	 * Enables the static usage of the connector. By default
	 */
	public static void enable() {
		getInstance().setEnabled(true);
	}

	/**
	 * Disable the static usage of the Connector.
	 */
	public static void disable() {
		getInstance().setEnabled(false);
	}

	private UnclosableConnection connection;
	private int connectionStack;
	private boolean enabled;
	private String connectionUrl;

	/**
	 * The default Connector will use the ConnectionManager to connect to the
	 * database.
	 */
	public Connector() {
		this(null);
	}

	/**
	 * The Connector wraps the specified connection string.
	 * 
	 * @param connectionUrl
	 *            the connection URL to use.
	 */
	public Connector(String connectionUrl) {
		this.connectionUrl = connectionUrl;
		this.connection = null;
		this.connectionStack = 0;
		this.enabled = true;
	}

	/**
	 * Gets if the current connector is enabled.
	 * 
	 * @return true if enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled property to true or false. A disabled connector will
	 * throw an IllegalStateException if any of it's methods (other than
	 * isEnabled or setEnebled) is called.
	 * 
	 * @param enabled
	 *            the state desired.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Tells how may connection have been opened through this connector.
	 * 
	 * @return the number of opened connections.
	 */
	public int getConnectionStackCount() {
		return this.connectionStack;
	}

	/**
	 * @return the connection url to the database.
	 */
	public String getConnectionUrl() {
		return this.connectionUrl;
	}

	/**
	 * Open a connection and returns it or increment an internal counter and
	 * return the already existing connection.
	 * 
	 * @return a connection to the database.
	 * 
	 * @throws SQLException
	 *             Thrown when a problem occurs during SQL execution.
	 */
	public Connection open() throws SQLException {
		if (!this.enabled) {
			throw new IllegalStateException(DISABLED_ERROR_STRING);
		}
		if (this.connectionStack == 0) {
			if (this.connectionUrl == null) {
				ConnectionManager cm = new ConnectionManager();
				this.connection = new UnclosableConnection(
						cm.createConnection());
			} else {
				this.connection = new UnclosableConnection(
						DriverManager.getConnection(this.connectionUrl));
			}
			this.connection.setAutoCommit(false);
		}
		this.connectionStack++;
		return this.connection;
	}

	/**
	 * Commits the current transaction and closes a the current connection if
	 * the connection counter has reached 0. Otherwise, decrement the connection
	 * counter.
	 */
	public void close() {
		if (!this.enabled) {
			throw new IllegalStateException(DISABLED_ERROR_STRING);
		}
		if (this.connectionStack == 0)
			return;
		try {
			this.connectionStack--;
			if (this.connectionStack == 0) {
				this.connection.commit();
				this.connection.getInternalConnection().close();
				this.connection = null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Rolls back the current transaction, close the current connection, set the
	 * connection counter to 0 and set the connection to null.
	 */
	public void rollback() {
		if (!this.enabled) {
			throw new IllegalStateException(DISABLED_ERROR_STRING);
		}
		try {
			if (this.connectionStack > 0) {
				this.connectionStack = 0;
				this.connection.rollback();
				this.connection.getInternalConnection().close();
				this.connection = null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the current connection.
	 * 
	 * @return the current connection.
	 * 
	 * @deprecated Use the connection returned by open() instead.
	 */
	@Deprecated
	public Connection getConnection() {
		if (this.connection == null)
			return null;
		return this.connection;
	}
}
