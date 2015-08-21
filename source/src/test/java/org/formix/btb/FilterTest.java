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
import java.sql.SQLNonTransientConnectionException;

import org.formix.btb.Comparators;
import org.formix.btb.ConnectionManager;
import org.formix.btb.LogicalFilter;
import org.formix.btb.Operators;
import org.formix.btb.PropertyFilter;
import org.formix.btb.Util;
import org.formix.btb.types.Employee;
import org.formix.btb.utils.DatabaseManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilterTest {

	private static DatabaseManager dbm;
	private static String dbName = "data/testdb";

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
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleFilter() {
		String expected = "(name <> ?)";

		PropertyFilter cf = new PropertyFilter("name",
				Comparators.DIFFERENT, "Robert Johnson");

		String actual = cf.toQueryString();

		assertEquals("actual should be " + expected + " instead of " + actual,
				expected, actual);
	}

	@Test
	public void testComplexFilter() {
		String expected = "((Employee.name <> ?) OR (sin = ?) OR ((wages >= ?) AND (wages < ?)))";

		PropertyFilter cf1 = new PropertyFilter(Employee.class, "name",
				Comparators.DIFFERENT, "Robert Johnson");

		PropertyFilter cf2 = new PropertyFilter("sin",
				Comparators.EQUAL, "123 345 567");

		PropertyFilter cf3 = new PropertyFilter("wages",
				Comparators.GREATER_OR_EQUAL, 10);

		PropertyFilter cf4 = new PropertyFilter("wages", Comparators.LOWER, 20);

		LogicalFilter filter = new LogicalFilter(Operators.OR);
		filter.getFilters().add(cf1);
		filter.getFilters().add(cf2);
		filter.getFilters().add(new LogicalFilter(Operators.AND, cf3, cf4));

		String actual = filter.toString();

		assertEquals("actual should be " + expected + " instead of " + actual,
				expected, actual);
	}
}
