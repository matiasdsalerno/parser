package com.ef;

import com.ef.config.CommandLineConfig;
import com.ef.dao.AccessLogEntryDao;
import com.ef.dao.BlockedIpAddressDao;
import com.ef.dao.pool.MySqlConnectionFactory;
import com.ef.model.ScanDuration;
import org.apache.commons.cli.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ef.config.CommandLineConfig.*;

public class Parser {


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        GenericObjectPool<Connection> connectionPool = createConnectionPool();

        AccessLogEntryDao accessLogEntryDao = new AccessLogEntryDao(connectionPool);

        BlockedIpAddressDao blockedIpAddressDao = new BlockedIpAddressDao(connectionPool);

        Options options = new CommandLineConfig().buildCommandLineOptions();

        CommandLine cmd;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
            String accessLogPath = cmd.getOptionValue(ACCESSLOG_ARG_KEY);
            ScanDuration scanDuration = ScanDuration.fromArgumentId(cmd.getOptionValue(DURATION_ARG_KEY));
            Integer threshold = Integer.valueOf(cmd.getOptionValue(THRESHOLD_ARG_KEY));
            LocalDateTime beginTime = LocalDateTime.from(DATE_TIME_FORMATTER.parse(cmd.getOptionValue(START_DATE_ARG_KEY)));

            AccessLogParser accessLogParser = new AccessLogParser(accessLogPath, accessLogEntryDao, blockedIpAddressDao);

            accessLogParser.loadIPAddressesAndBlock(beginTime, scanDuration, threshold);

        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Access Log Parser", options);
        }

    }

    private static GenericObjectPool<Connection> createConnectionPool() {
        GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(20);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(10000);
        config.setMinEvictableIdleTimeMillis(60000);
        return new GenericObjectPool<Connection>(new MySqlConnectionFactory(), config);
    }

}
