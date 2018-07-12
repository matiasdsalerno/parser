package com.ef;

import com.ef.dao.AccessLogEntryDao;
import com.ef.dao.BlockedIpAddressDao;
import com.ef.model.AccessLogEntry;
import com.ef.model.BlockedIpAddress;
import com.ef.model.ScanDuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessLogParserTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock private AccessLogEntryDao entryDao;
    @Mock private BlockedIpAddressDao blockedIpAddressDao;

    @Test
    public void loadIPAddressesAndBlock() throws Exception {
        File file = tempFolder.newFile("access.log");

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
        outputStreamWriter.write("2017-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:12.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:13.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:14.763|192.168.234.83|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:15.763|192.168.234.83|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"");
        outputStreamWriter.close();

        AccessLogParser accessLogParser = new AccessLogParser(file.getAbsolutePath(), entryDao, blockedIpAddressDao);

        List<BlockedIpAddress> blockedIpAddresses = accessLogParser.loadIPAddressesAndBlock(
                LocalDateTime.of(
                        2017,
                        1,
                        1,
                        0,
                        0,
                        0), ScanDuration.HOURLY, 2);

        assertEquals(1, blockedIpAddresses.size());
        assertEquals("192.168.234.82", blockedIpAddresses.get(0).getKey());

        verify(blockedIpAddressDao).save(anyListOf(BlockedIpAddress.class));

        // Expected to save all the accesses into the DB
        ArgumentCaptor<List> tArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(entryDao).save(tArgumentCaptor.capture());
        assertEquals(5, tArgumentCaptor.getValue().size());

    }

    @Test
    public void loadIPAddressesAndBlockWhenFileIsEmpty() throws Exception {
        File file = tempFolder.newFile("access.log");

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
        outputStreamWriter.write("2017-01-01 00:00:11.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:12.763|192.168.234.82|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.write("2017-01-01 00:00:14.763|192.168.234.83|\"GET / HTTP/1.1\"|200|\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"\n");
        outputStreamWriter.close();

        AccessLogParser accessLogParser = new AccessLogParser(file.getAbsolutePath(), entryDao, blockedIpAddressDao);

        List<BlockedIpAddress> blockedIpAddresses = accessLogParser.loadIPAddressesAndBlock(
                LocalDateTime.of(
                        2017,
                        1,
                        1,
                        0,
                        0,
                        0), ScanDuration.HOURLY, 2);

        assertEquals(0, blockedIpAddresses.size());

        // Expected to save all the accesses into the DB
        ArgumentCaptor<List> tArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(entryDao).save(tArgumentCaptor.capture());
        assertEquals(3, tArgumentCaptor.getValue().size());

        // Expect that the blocked ips dao is not called, because there were no threshold violation
        verify(blockedIpAddressDao, never()).save(anyListOf(BlockedIpAddress.class));
    }

    @Test
    public void loadIPAddressesAndBlockWhenNoThresholdViolation() throws Exception {
        File file = tempFolder.newFile("access.log");

        AccessLogParser accessLogParser = new AccessLogParser(file.getAbsolutePath(), entryDao, blockedIpAddressDao);

        List<BlockedIpAddress> blockedIpAddresses = accessLogParser.loadIPAddressesAndBlock(
                LocalDateTime.of(
                        2017,
                        1,
                        1,
                        0,
                        0,
                        0), ScanDuration.HOURLY, 2);

        assertEquals(0, blockedIpAddresses.size());
        ArgumentCaptor<List> tArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(entryDao, never()).save(tArgumentCaptor.capture());
        verify(blockedIpAddressDao, never()).save(anyListOf(BlockedIpAddress.class));

    }

    @Test(expected = RuntimeException.class)
    public void loadIPAddressesAndBlockWhenFileDoesntExist() throws Exception {

        AccessLogParser accessLogParser = new AccessLogParser("non-existent-path", entryDao, blockedIpAddressDao);

        accessLogParser.loadIPAddressesAndBlock(
                LocalDateTime.of(
                        2017,
                        1,
                        1,
                        0,
                        0,
                        0), ScanDuration.HOURLY, 2);
    }

}