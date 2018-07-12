package com.ef.dao;

import com.ef.dao.exception.DaoException;
import org.apache.commons.pool2.ObjectPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public abstract class AbstractDao<T> {

    private ObjectPool<Connection> connectionPool;
    private String insertStatement;

    public AbstractDao(ObjectPool<Connection> connectionPool, String insertStatement) {
        this.connectionPool = connectionPool;
        this.insertStatement = insertStatement;
    }

    public void save(List<T> objects) {
        if(Objects.isNull(objects) || objects.isEmpty()) {
            return;
        }
        Connection connection = null;
        try {
            connection = connectionPool.borrowObject();
            try(PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {
                addBatches(objects, preparedStatement);
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            throw new DaoException("Error saving data into " + objects.get(0).getClass(), e);
        } catch (Exception e) {
            throw new DaoException("Error while borrowing connection from pool", e);
        } finally {
            returnConnectionSafely(connection);
        }
    }

    private void returnConnectionSafely(Connection connection) {
        if(!Objects.isNull(connection)) {
            try {
                connectionPool.returnObject(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addBatches(List<T> objects, PreparedStatement preparedStatement) {
        try {
            for (T entry: objects) {
                setParametersInStatment(preparedStatement, entry);
                preparedStatement.addBatch();
            }
        } catch (SQLException e) {
            throw new DaoException("Error while creating batch for batch statement", e);
        }
    }

    protected abstract void setParametersInStatment(PreparedStatement preparedStatement, T entry) throws SQLException;

}
