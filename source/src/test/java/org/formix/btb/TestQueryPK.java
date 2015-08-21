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

import java.io.File;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;

import org.formix.btb.Bridge;
import org.formix.btb.ConnectionManager;
import org.formix.btb.Join;
import org.formix.btb.Query;
import org.formix.btb.Util;
import org.formix.btb.types.Department;
import org.formix.btb.types.Employee;
import org.formix.btb.types.IntKey;
import org.formix.btb.types.StringKey;
import org.formix.btb.utils.DatabaseManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestQueryPK {

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

	@Test
	public void testExecute_PK_Int() throws Exception {
		Bridge<IntKey> bridge = new Bridge<IntKey>(IntKey.class);
		Query<IntKey> query = bridge.newQuery();
		System.out.println(query);
		List<IntKey> list = query.execute();
		System.out.println("\t" + list);
	}

	@Test
	public void testExecute_PK_String() throws Exception {
		Bridge<StringKey> bridge = new Bridge<StringKey>(StringKey.class);
		Query<StringKey> query = bridge.newQuery();
		System.out.println(query);
		List<StringKey> list = query.execute();
		System.out.println("\t" + list);
	}

	@Test
	public void testQueryJoin() throws Exception {
		Query<StringKey> query = new Query<StringKey>(
				"SELECT * FROM StringKey", StringKey.class);
		query.getJoins().add(
				new Join(StringKey.class, IntKey.class, "id", "stringKeyId"));
		query.getJoins().add(
				new Join(Department.class, Employee.class, "id", "departmentId"));
		System.out.println(query);
	}

}
