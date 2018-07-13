package com.ef.dao.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Class used to create MySQL Connections.
 */
public class MySqlConnectionFactory extends BasePooledObjectFactory<Connection> {

    private final String user;
    private final String password;
    private final String connectionString;

    public MySqlConnectionFactory(String host, String port, String schema, String timeZone, String user, String password) {
        this.connectionString = "jdbc:mysql://" + host + ":"+port + "/" + schema + "?serverTimezone=" + timeZone;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection create() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(connectionString, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating MySQL Connection pool", e);
        }
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }
}
