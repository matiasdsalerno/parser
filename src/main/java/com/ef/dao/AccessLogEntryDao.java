package com.ef.dao;

import com.ef.model.AccessLogEntry;
import org.apache.commons.pool2.ObjectPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AccessLogEntryDao extends AbstractDao<AccessLogEntry> {

    protected static final String INSERT_INTO_ACCESS_ENTRY_DATE_TIME_IP_ADDRESS_REQUEST_VALUES = "INSERT INTO access_entry(date_time, " +
            "ip_address, " +
            "request, " +
            "status, " +
            "user_agent) " +
            "VALUES (?, ?, ?, ?, ?)";

    public AccessLogEntryDao(ObjectPool<Connection> connectionPool) {
        super(connectionPool, INSERT_INTO_ACCESS_ENTRY_DATE_TIME_IP_ADDRESS_REQUEST_VALUES);
    }

    protected void setParametersInStatment(PreparedStatement preparedStatement, AccessLogEntry entry) throws SQLException {
        preparedStatement.setTimestamp(1, Timestamp.valueOf(entry.getTime()));
        preparedStatement.setString(2, entry.getIpAddress());
        preparedStatement.setString(3, entry.getRequest());
        preparedStatement.setInt(4,entry.getStatusCode());
        preparedStatement.setString(5,entry.getUserAgent());
    }
}
