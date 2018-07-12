package com.ef.dao;

import com.ef.dao.exception.DaoException;
import com.ef.model.AccessLogEntry;
import com.ef.model.BlockedIpAddress;
import org.apache.commons.pool2.ObjectPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlockedIpAddressDaoTest {

    @Mock private Connection connection;
    @Mock private ObjectPool<Connection> connectionPool;
    @Mock private PreparedStatement statement;

    @InjectMocks
    private BlockedIpAddressDao blockedIpAddressDao;

    @Test
    public void testSaveWhenNull() throws Exception {
        blockedIpAddressDao.save(null);

        verify(connectionPool, never()).borrowObject();
    }

    @Test
    public void testSaveWhenEmpty() throws Exception {
        blockedIpAddressDao.save(Collections.emptyList());

        verify(connectionPool, never()).borrowObject();
    }

    @Test
    public void testSave() throws Exception {
        given(connectionPool.borrowObject()).willReturn(connection);
        given(connection.prepareStatement(BlockedIpAddressDao.INSERT_INTO_BLOCKED_IP_ADDRESS_IP_ADDRESS_MESSAGE_VALUES)).willReturn(statement);

        blockedIpAddressDao.save(Collections.singletonList(new BlockedIpAddress("192.168.234.82", "Ip blocked")));

        verify(statement).setString(1,"192.168.234.82");
        verify(statement).setString(2,"Ip blocked");
        verify(statement).addBatch();
        verify(statement).executeBatch();
        verify(connectionPool).borrowObject();

    }

}