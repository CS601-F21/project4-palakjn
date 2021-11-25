package controllers;

import configuration.Config;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static BasicDataSource ds;

    public static void init() {
        if(ds == null) {
            ds = new BasicDataSource();
            ds.setUrl(Config.getUrl());
            ds.setUsername(Config.getUsername());
            ds.setPassword(Config.getPassword());
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
        }
    }

    /**
     * Return a Connection from the pool.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
