package org.formix.btb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLNonTransientConnectionException;

import org.formix.btb.ConnectionManager;
import org.formix.btb.Connector;
import org.formix.btb.Util;
import org.formix.btb.utils.DatabaseManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class TestConnector {

    private static DatabaseManager dbm;
    private static String          dbName = "data/testdb";

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
	
	
	@Test
	public void TestConnect() throws Exception {
		Connector connector = Connector.getInstance();
		Connection conn = connector.open();
		connector.open();
		PreparedStatement stmt = conn.prepareStatement("select * from StringKey");
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			Assert.fail("La commande ne s'est pas exécutée correctement.");
		connector.close();
		connector.close();
	}
	
}
