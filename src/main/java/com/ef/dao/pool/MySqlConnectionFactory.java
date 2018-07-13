package com.ef.dao.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Class used to create MySQL Connections.
 * Connection data, such as hostname, port, schema, user, password, serverTimeZone, etc.
 * should be loaded from properties files, not harcoded in the class
 */
public class MySqlConnectionFactory extends BasePooledObjectFactory<Connection> {

    private static final String PASS = "parser123";
    private static final String USER = "parser";

    @Override
    public Connection create() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/parser?serverTimezone=America/Buenos_Aires", USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating MySQL Connection pool", e);
        }
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }
}
