package com.ef.dao;

import com.ef.dao.exception.DaoException;
import com.ef.model.AccessLogEntry;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
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

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessLogEntryDaoTest {

    @Mock private Connection connection;
    @Mock private ObjectPool<Connection> connectionPool;
    @Mock private PreparedStatement statement;

    @InjectMocks
    private AccessLogEntryDao accessLogEntryDao;

    @Test
    public void testSaveWhenNull() throws Exception {
        accessLogEntryDao.save(null);

        verify(connectionPool, never()).borrowObject();
    }

    @Test
    public void testSaveWhenEmpty() throws Exception {
        accessLogEntryDao.save(Collections.emptyList());

        verify(connectionPool, never()).borrowObject();
    }

    @Test
    public void testSave() throws Exception {
        given(connectionPool.borrowObject()).willReturn(connection);
        given(connection.prepareStatement(AccessLogEntryDao.INSERT_INTO_ACCESS_ENTRY_DATE_TIME_IP_ADDRESS_REQUEST_VALUES)).willReturn(statement);

        accessLogEntryDao.save(Collections.singletonList(new AccessLogEntry("2017-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"")));

        verify(statement).setTimestamp(eq(1),any(Timestamp.class));
        verify(statement).setString(2,"192.168.234.82");
        verify(statement).setString(3,"\"GET / HTTP/1.1\"");
        verify(statement).setInt(4,200);
        verify(statement).setString(5,"\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        verify(statement).addBatch();
        verify(statement).executeBatch();
        verify(connectionPool).borrowObject();

    }

    @Test(expected = DaoException.class)
    public void testSaveWhenSQLException() throws Exception {
        given(connectionPool.borrowObject()).willReturn(connection);
        given(connection.prepareStatement(AccessLogEntryDao.INSERT_INTO_ACCESS_ENTRY_DATE_TIME_IP_ADDRESS_REQUEST_VALUES)).willThrow(new SQLException());

        accessLogEntryDao.save(Collections.singletonList(new AccessLogEntry("2017-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"")));
    }

}