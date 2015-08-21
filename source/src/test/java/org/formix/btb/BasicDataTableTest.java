/**
 * Copyright 2008 Jean-Philippe Gravel, eng. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
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

import static junit.framework.Assert.*;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.formix.btb.Bridge;
import org.formix.btb.ConnectionManager;
import org.formix.btb.SqlDescriptor;
import org.formix.btb.Util;
import org.formix.btb.types.Employee;
import org.formix.btb.types.EmployeeEntity;
import org.formix.btb.types.EmployeeTable;
import org.formix.btb.types.EmployeeWithState;
import org.formix.btb.utils.DatabaseManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicDataTableTest {

	private SqlDescriptor sqlDescriptor;
	private SortedSet<String> columns;
	private static DatabaseManager dbm;
	private static String dbName = "data/testdb";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File testDB = new File(dbName);
		boolean testDBExists = testDB.exists();

		if (!testDBExists) {
			dbm = new DatabaseManager();
			dbm.createDatabase(new File("TestDB.sql"));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			ConnectionManager mgr = new ConnectionManager();
			mgr.createConnection("derbyShutdown");
		} catch (SQLNonTransientConnectionException e) {
			System.out.println("Connection closed.");
		}
		Util.delete(new File("data"));
	}

	public BasicDataTableTest() throws Exception {
		Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
		sqlDescriptor = bridge.getDescriptor();

		columns = new TreeSet<String>();
		columns.add("birthDate");
		columns.add("departmentId");
		columns.add("id");
		columns.add("name");
		columns.add("sin");
		columns.add("wages");
	}

	@Test
	public void testTestAlternativeTypes() throws SQLException {
		Bridge<EmployeeTable> bridgeTable = new Bridge<EmployeeTable>(
				EmployeeTable.class);
		SqlDescriptor descTable = bridgeTable.getDescriptor();
		assertEquals(
				"The tableName from the sqlDescriptor should be Employee " +
				"when a classe named EmployeeTable is used.",
				"Employee", descTable.getTableName());
		
		Bridge<EmployeeEntity> bridgeEntity = new Bridge<EmployeeEntity>(
				EmployeeEntity.class);
		SqlDescriptor descEntity = bridgeEntity.getDescriptor();
		assertEquals(
				"The tableName from the sqlDescriptor should be Employee " +
				"when a classe named EmployeeTable is used.",
				"Employee", descEntity.getTableName());

	}

	@Test
	public void testGetTableName() {
		String name = sqlDescriptor.getTableName();
		assertEquals("name should be Employee instead of " + name, "Employee",
				name);
	}

	@Test
	public void testGetPrimaryKey() {
		String expected = "id";
		String primKey = sqlDescriptor.getPrimaryKey();
		assertEquals("primKey should be id instead of " + primKey, expected,
				primKey);
	}

	@Test
	public void testGetSetValue() throws Exception {
		Employee e1 = new Employee();
		e1.setName("Robert Walsh");
		e1.setSin("123 345 567");
		e1.setWages(14.35);

		Employee e2 = new Employee();
		for (String property : columns) {
			if (sqlDescriptor.isAccessible(property)) {
				Object value = sqlDescriptor.getValue(e1, property);
				sqlDescriptor.setValue(e2, property, value);
			} else {
				System.out.println("INACCESSIBLE: " + property);
			}

		}

		assertEquals("e2 should be equal to e1.", e1, e2);
	}

	@Test
	public void testGetColumns() {
		assertEquals(
				"The columns set and descriptor.getColumns should be equal.",
				new HashSet<String>(columns),
				new HashSet<String>(sqlDescriptor.getColumns()));
	}

	@Test
	public void testGetSelectQuery() {

		String expectedQuery = "SELECT ";
		int i = 0;
		for (String property : columns) {
			if (i > 0) {
				expectedQuery += ", ";
			}
			expectedQuery += "Employee." + property;
			i++;
		}
		expectedQuery += " FROM Employee";

		String actualQuery = sqlDescriptor.getSelectQuery();

		assertEquals("the actual query should be " + expectedQuery
				+ " instead of " + actualQuery, expectedQuery, actualQuery);
	}

	@Test
	public void testGetInsertQuery() {
		String expectedQuery = "INSERT INTO Employee ";
		String cols = "(";
		int i = 0;
		
		String values = " VALUES (";

		for (String column : columns) {

			if (sqlDescriptor.isReadOnly(column)) {
				continue;
			}

			if (i > 0) {
				cols += ", ";
				values += ", ";
			}
			
			values += "?";
			cols += column;
			i++;
		}

		values += ")";
		cols += ")";
		expectedQuery += cols + values;

		String actualQuery = sqlDescriptor.getInsertQuery();

		assertEquals("the actual query should be " + expectedQuery
				+ " instead of " + actualQuery, expectedQuery, actualQuery);
	}

	@Test
	public void testGetUpdateQuery() {
		String expectedQuery = "UPDATE Employee SET ";
		int i = 0;

		for (String column : columns) {

			if (sqlDescriptor.getPrimaryKey().equals(column)) {
				continue;
			}

			if (i > 0) {
				expectedQuery += ", ";
			}

			expectedQuery += column + " = ?";
			i++;
		}

		expectedQuery += " WHERE id = ?";

		String actualQuery = sqlDescriptor.getUpdateQuery();

		assertEquals("the actual query should be " + expectedQuery
				+ " instead of " + actualQuery, expectedQuery, actualQuery);
	}

	@Test
	public void testGetDeleteQuery() {
		String expectedQuery = "DELETE FROM Employee WHERE id = ?";
		String actualQuery = sqlDescriptor.getDeleteQuery();
		assertEquals("the actual query should be " + expectedQuery
				+ " instead of " + actualQuery, expectedQuery, actualQuery);
	}

	@Test
	public void testToString() {
		System.out.println(sqlDescriptor);
	}

	@Test
	public void testIgnoredProperties() throws Exception {
		Bridge<EmployeeWithState> bridge = new Bridge<EmployeeWithState>(
				EmployeeWithState.class, "state");
		SqlDescriptor sqlDescriptor = bridge.getDescriptor();

		assertEquals(
				"The columns set and descriptor.getColumns should be equal.",
				new HashSet<String>(columns),
				new HashSet<String>(sqlDescriptor.getColumns()));
	}
}
