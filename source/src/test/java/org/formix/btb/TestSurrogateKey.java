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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.formix.btb.Bridge;
import org.formix.btb.ConnectionManager;
import org.formix.btb.Query;
import org.formix.btb.Util;
import org.formix.btb.types.Department;
import org.formix.btb.types.Employee;
import org.formix.btb.types.EmployeeEx;
import org.formix.btb.utils.DatabaseManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSurrogateKey {

	private static DatabaseManager dbm;
	private static String dbName = "data/testdb";

	private ConnectionManager connMgr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File testDB = new File(dbName);
		boolean testDBExists = testDB.exists();

		dbm = new DatabaseManager();
		if (!testDBExists) {
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

	@Before
	public void setUp() throws Exception {
		Connection connection = this.connMgr.createConnection();
		PreparedStatement stmt = connection
				.prepareStatement("DELETE FROM employee");
		stmt.executeUpdate();
	}

	@After
	public void tearDown() throws Exception {
	}

	public TestSurrogateKey() {
		this.connMgr = new ConnectionManager();
	}

	@Test
	public void insertTest() throws Exception {
		Employee e = insertEmployee("Gravel, Jean-Philippe", "123 345 567",
				43500, new GregorianCalendar(1972, 7, 22));
		assertTrue("Primary key: id should be grater than 0.", e.getId() > 0);
	}

	@Test
	public void updateTest() throws Exception {

		String oldName = "Gravel, Jean-Philippe";
		Employee e = insertEmployee(oldName, "123 345 567", 43500);
		String name = readName(e.getId());
		assertEquals(oldName, name);

		Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
		String newName = "Thurston, Georges";
		e.setName(newName);
		bridge.update(e);

		name = readName(e.getId());
		assertEquals(newName, name);
	}

	@Test
	public void deleteTest() throws Exception {
		String oldName = "Gravel, Jean-Philippe";
		Employee e = insertEmployee(oldName, "123 345 567", 43500);

		Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
		bridge.delete(e);

		assertEquals("The employee " + e + " still exists in the table.", 0,
				countEmployee());
	}

	@Test
	public void differentTypesTest() throws Exception {
		Department d = insertDepartment("Sales");

		insertEmployee("name1", "123 234 345", 12, d);
		insertEmployee("name2", "aaa ccc ddd", 13, d);
		insertEmployee("name3", "xxx ddd ggg", 14, d);
		insertEmployee("name4", "qqq eee rrr", 15, d);

		ArrayList<EmployeeEx> list = new ArrayList<EmployeeEx>();
		Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
		Query<EmployeeEx> query = bridge.newQuery(EmployeeEx.class);
		query.executeFill(list);

		System.out.println(list);
	}

	private int countEmployee() throws Exception {
		int count = 0;
		Connection conn = this.connMgr.createConnection();
		PreparedStatement stmt = conn
				.prepareStatement("SELECT COUNT(*) FROM employee");
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		conn.close();
		return count;
	}

	private String readName(int id) throws Exception {

		String name = null;
		Connection conn = this.connMgr.createConnection();
		PreparedStatement stmt = conn
				.prepareStatement("SELECT name FROM employee WHERE id = ?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			name = rs.getString("name");
		}
		rs.close();
		stmt.close();
		conn.close();
		return name;
	}

	private Employee insertEmployee(String name, String sin, double wages)
			throws Exception {
		return insertEmployee(name, sin, wages, null, null);
	}

	private Employee insertEmployee(String name, String sin, double wages,
			Calendar birthDate) throws Exception {
		return insertEmployee(name, sin, wages, null, birthDate);
	}

	private Employee insertEmployee(String name, String sin, double wages,
			Department d) throws Exception {
		return this.insertEmployee(name, sin, wages, d, null);
	}

	private Employee insertEmployee(String name, String sin, double wages,
			Department d, Calendar birthDate) throws Exception {

		EmployeeEx e = new EmployeeEx();
		e.setName(name);
		e.setSin(sin);
		e.setWages(wages);
		e.setDepartment(d);

		Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
		bridge.insert(e);

		return e;
	}

	private Department insertDepartment(String name) throws Exception {
		Department d = new Department();
		d.setName(name);

		Bridge<Department> bridge = new Bridge<Department>(Department.class);
		bridge.insert(d);

		return d;
	}
}
