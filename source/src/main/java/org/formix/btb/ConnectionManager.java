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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>
 * Manages database URLs contained in the database.properties file. It's
 * possible to define more than one database URL in the file or to load
 * connection attributes from another file.
 * </p>
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
 * <p>
 * You can change the path of the database.properties file by setting the system
 * property attribute "databases.properties.path" to the desired file path.
 * </p>
 * 
 * @author Jean-Philippe Gravel
 */
public class ConnectionManager {

	/**
	 * The default attribute file.
	 */
	private final String[] FILE_PATH = new String[] { "bridge.properties",
			"bridge.xml", "databases.properties", "databases.xml" };

	private final String FILE_PATH_PROP = "databases.properties.path";
	private final String COMMENT = "Connection strings";
	private final String DATABASE_URL = "database.url";
	private final String DATABASE_DEFAULT = "database.default";

	private static Properties defaultProperties = null;

	private File file;
	private Properties properties;

	/**
	 * Constructor of the class.
	 */
	public ConnectionManager() {

		if (defaultProperties != null) {
			properties = defaultProperties;
			return;
		}

		properties = new Properties();
		properties.put(DATABASE_URL, "");
		properties.put(DATABASE_DEFAULT, "");

		this.initalizeFile();

		if (this.file.exists()) {
			try {
				loadAttributes(this.file.getAbsolutePath());
			} catch (IOException ex) {
				// The file should exist at this point. If it's not readable,
				// we encountered an unexpected exception.
				throw new RuntimeException(ex);
			}

		}
	}

	private void initalizeFile() {
		int fpIndex = 0;
		String fp = System.getProperty(FILE_PATH_PROP, FILE_PATH[fpIndex]);

		fpIndex++;
		File f = new File(fp);
		while (!f.exists() && fpIndex < FILE_PATH.length) {
			f = new File(FILE_PATH[fpIndex]);
			fpIndex++;
		}
		this.file = f;
	}

	/**
	 * Loads the default attribute file as defined by the system property
	 * "databases.properties.path". If not defined, uses the default value
	 * "databases.xml";
	 * 
	 * @throws IOException
	 *             if the file does not exist.
	 */
	public void loadAttributes() throws IOException {
		loadAttributes(this.file.getAbsolutePath());
	}

	/**
	 * Load the specified attribute file. Can read both XML and former attribute
	 * files. Note that a XML file must have the extension ".xml" or
	 * ".properties" to be recognized.
	 * 
	 * @param filePath
	 *            Path to the file to load.
	 * @throws IOException
	 *             if the file does not exist.
	 */
	public void loadAttributes(String filePath) throws IOException {
		properties.clear();
		this.file = new File(filePath);

		FileInputStream fis = new FileInputStream(this.file);
		if (filePath.toLowerCase().endsWith(".xml")) {
			properties.loadFromXML(fis);
		} else {
			properties.load(fis);
		}
		fis.close();
	}

	/**
	 * Saves the default attribute file as defined by
	 * ConnectionManager.FILE_PATH.
	 * 
	 * @throws IOException
	 *             if there is a problem during the save operation.
	 */
	public void saveAttributes() throws IOException {
		saveAttributes(this.file.getAbsolutePath());
	}

	/**
	 * Saves the changes to the attribute file specified. Note that a XML file
	 * must have the extension ".xml" or ".properties" to be recognized.
	 * 
	 * @param filePath
	 *            The path to the file to be saved.
	 * @throws IOException
	 *             if there is a problem during the save operation.
	 */
	public void saveAttributes(String filePath) throws IOException {
		this.file = new File(filePath);

		FileOutputStream fos = new FileOutputStream(file);
		if (filePath.toLowerCase().endsWith("xml")) {
			properties.storeToXML(fos, COMMENT);
		} else {
			properties.store(fos, COMMENT);
		}
		fos.close();
	}

	/**
	 * Creates a connection using the default connection string.
	 * 
	 * @return a new connection.
	 * @throws SQLException
	 *             if there is a problem connecting to the database or if the
	 *             connection string is invalid.
	 */
	public Connection createConnection() throws SQLException {
		return createConnection("");
	}

	/**
	 * Creates a connection using the specified connection name.
	 * 
	 * @param name
	 *            The name of the connection to create.
	 * @return a new connection.
	 * @throws SQLException
	 *             if there is a problem connecting to the database or if the
	 *             connection string is invalid.
	 */
	public Connection createConnection(String name) throws SQLException {
		Util.throwIfNull(name, "name");
		String url = getUrl(name);
		Connection conn = null;
		conn = DriverManager.getConnection(url);
		return conn;
	}

	/**
	 * Get the file path to the actual database property file.
	 * 
	 * @return the file path to the actual database property file.
	 */
	public String getFilePath() {
		return this.file.getAbsolutePath();
	}

	/**
	 * Get the specified connection string.
	 * 
	 * @param name
	 *            The name of the connection string to get.
	 * @return a String containing the connection information, including the
	 *         protocol.
	 */
	public String getUrl(String name) {
		Util.throwIfNull(name, "name");

		String connName = name;
		if (connName.equals("")) {
			connName = properties.getProperty(DATABASE_DEFAULT, "");
		}

		String url = properties.getProperty(DATABASE_URL);
		if (!connName.equals("")) {
			url = properties.getProperty(DATABASE_URL + "." + connName);
		}

		return url;
	}

	/**
	 * Sets the url value to the database.url.{name} attribute.
	 * 
	 * @param name
	 *            The name of the connection.url to set.
	 * @param url
	 *            the url value.
	 */
	public void setUrl(String name, String url) {
		Util.throwIfNull(name, "name");

		if (name.equals("")) {
			name = properties.getProperty(DATABASE_DEFAULT);
		}

		if (!name.equals("")) {
			properties.setProperty(DATABASE_URL + "." + name, url);
		} else {
			properties.setProperty(DATABASE_URL, url);
		}
	}

	/**
	 * Gets the default database URL (the database.url.{name} value).
	 * 
	 * @return a <code>String</code> containing the database URL.
	 */
	public String getUrl() {
		return getUrl("");
	}

	/**
	 * Sets the default database URL.
	 * 
	 * @param url
	 *            The url to set to the default database.url attribute.
	 */
	public void setUrl(String url) {
		setUrl("", url);
	}

	/**
	 * Gets the default database.url name.
	 * 
	 * @return the database.url name.
	 */
	public String getDefault() {
		return properties.getProperty(DATABASE_DEFAULT);
	}

	/**
	 * Sets the default database url name.
	 * 
	 * @param name
	 *            The name of the default database.url to use.
	 */
	public void setDefault(String name) {
		properties.setProperty(DATABASE_DEFAULT, name);
	}
}
