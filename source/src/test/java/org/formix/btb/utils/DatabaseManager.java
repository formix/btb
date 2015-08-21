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
package org.formix.btb.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.formix.btb.ConnectionManager;

public class DatabaseManager {

    private Connection createConnection() throws SQLException {
        ConnectionManager manager = new ConnectionManager();
        return manager.createConnection("derbycreate");
    }

    public void createDatabase(String ddl) throws SQLException {

        Connection connection = createConnection();

        String[] queries = ddl.split(";");
        Statement stmt = connection.createStatement();

        for (String query : queries) {
            if (!query.trim().equals("")) {
                stmt.addBatch(query.trim());
            }
        }

        stmt.executeBatch();

        stmt.close();
        connection.close();
    }

    public void createDatabase(File ddlFile) throws IOException, SQLException {
        String ddl = readDatabaseScript(ddlFile);
        createDatabase(ddl);
    }

    private String readDatabaseScript(File ddlFile) throws IOException {

        FileReader fr = new FileReader(ddlFile);
        char[] buf = new char[(int)ddlFile.length()];
        fr.read(buf);
        fr.close();
        String ddl = new String(buf);

        return ddl;
    }
}
