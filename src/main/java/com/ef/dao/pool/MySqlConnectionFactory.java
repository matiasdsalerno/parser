package com.ef.dao.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlConnectionFactory extends BasePooledObjectFactory<Connection> {

    private static final String PASS = "parser123";
    public static final String USER = "parser";

    @Override
    public Connection create() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/parser?serverTimezone=UTC", USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating MySQL Connection pool");
        }
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }
}
