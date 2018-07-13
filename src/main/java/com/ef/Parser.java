package com.ef;

import com.ef.config.CommandLineConfig;
import com.ef.dao.AccessLogEntryDao;
import com.ef.dao.BlockedIpAddressDao;
import com.ef.dao.pool.MySqlConnectionFactory;
import com.ef.model.ScanDuration;
import org.apache.commons.cli.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static com.ef.config.CommandLineConfig.*;

public class Parser {


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Properties properties = loadPropertiesFile();

        GenericObjectPool<Connection> connectionPool = createConnectionPool(properties);

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
        } finally {
            System.exit(0);
        }

    }

    private static Properties loadPropertiesFile() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("./parser.properties")){

            properties.load(input);
            // load a properties file
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private static GenericObjectPool<Connection> createConnectionPool(Properties properties) {
        GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(Integer.parseInt(properties.getProperty("db.conn.pool.max")));
        config.setTestOnBorrow(Boolean.parseBoolean(properties.getProperty("db.conn.pool.borrow.test")));
        config.setTestWhileIdle(Boolean.parseBoolean(properties.getProperty("db.conn.pool.idle.test")));
        config.setTimeBetweenEvictionRunsMillis(Long.parseLong(properties.getProperty("db.conn.pool.eviction"))); //10000
        config.setMinEvictableIdleTimeMillis(Long.parseLong(properties.getProperty("db.conn.pool.evitable.idle.time")));//60000

        String host = properties.getProperty("db.conn.host");
        String port = properties.getProperty("db.conn.port");
        String schema = properties.getProperty("db.conn.schema");
        String timeZone = properties.getProperty("db.conn.time.zone");
        String user = properties.getProperty("db.conn.time.user");
        String password = properties.getProperty("db.conn.time.password");

        return new GenericObjectPool<>(new MySqlConnectionFactory(
                host,
                port,
                schema,
                timeZone,
                user,
                password
        ), config);
    }

}
