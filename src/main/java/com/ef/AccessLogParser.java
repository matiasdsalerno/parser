package com.ef;


import com.ef.dao.AccessLogEntryDao;
import com.ef.dao.BlockedIpAddressDao;
import com.ef.model.AccessLogEntry;
import com.ef.model.BlockedIpAddress;
import com.ef.model.ScanDuration;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccessLogParser {

    private final String filePath;
    private AccessLogEntryDao accessLogEntryDao;
    private BlockedIpAddressDao blockedIpAddressDao;

    public AccessLogParser(String filePath, AccessLogEntryDao accessLogEntryDao, BlockedIpAddressDao blockedIpAddressDao) {
        this.filePath = filePath;
        this.accessLogEntryDao = accessLogEntryDao;
        this.blockedIpAddressDao = blockedIpAddressDao;
    }

    public List<BlockedIpAddress> loadIPAddressesAndBlock(LocalDateTime beginTime, ScanDuration duration, Integer threshold) {

        LocalDateTime endTime = duration.calculateEndTime(beginTime);

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            List<AccessLogEntry> accessLogEntries = stream.map(AccessLogEntry::new)
                    .collect(Collectors.toList());

            if(accessLogEntries.isEmpty()) {
                return Collections.emptyList();
            }

            Lists.partition(accessLogEntries, 10000)
                    .stream()
                    .map(entry -> CompletableFuture.runAsync(() -> accessLogEntryDao.save(entry), Executors.newFixedThreadPool(50)))
                    .forEach(CompletableFuture::join);

            List<BlockedIpAddress> ipAddressCount = accessLogEntries.stream()
                    .filter(entry -> entry.getTime().isAfter(beginTime) && entry.getTime().isBefore(endTime))
                    .collect(Collectors.groupingBy(AccessLogEntry::getIpAddress,
                            Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > threshold)
                    .map(entry -> new BlockedIpAddress(entry.getKey(), String.format("Blocked due to exceeding the access threshold of %d between %s and %s.", threshold, Parser.DATE_TIME_FORMATTER.format(beginTime), Parser.DATE_TIME_FORMATTER.format(endTime))))
                    .collect(Collectors.toList());

            if (ipAddressCount.isEmpty()) {
                return Collections.emptyList();
            }
            blockedIpAddressDao.save(ipAddressCount);
            ipAddressCount.forEach(System.out::println);
            return ipAddressCount;
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing file with path: " + filePath, e);
        }
    }
}
