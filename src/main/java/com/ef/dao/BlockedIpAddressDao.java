package com.ef.dao;

import com.ef.model.BlockedIpAddress;
import org.apache.commons.pool2.ObjectPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockedIpAddressDao extends AbstractDao<BlockedIpAddress> {

    protected static final String INSERT_INTO_BLOCKED_IP_ADDRESS_IP_ADDRESS_MESSAGE_VALUES = "INSERT INTO blocked_ip_address(ip_address, message) VALUES (?, ?)";

    public BlockedIpAddressDao(ObjectPool<Connection> connectionPool) {
        super(connectionPool, INSERT_INTO_BLOCKED_IP_ADDRESS_IP_ADDRESS_MESSAGE_VALUES);
    }

    @Override
    protected void setParametersInStatment(PreparedStatement preparedStatement, BlockedIpAddress entry) throws SQLException {
        preparedStatement.setString(1, entry.getKey());
        preparedStatement.setString(2, entry.getMessage());
    }

}
