package com.ef.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AccessLogEntry {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final LocalDateTime time;
    private final String ipAddress;
    private final String userAgent;
    private final String request;
    private final Integer statusCode;

    public AccessLogEntry(String accessLogLine) {
        String[] split = accessLogLine.split("\\|");
        this.time = LocalDateTime.from(DATE_TIME_FORMATTER.parse(split[0]));
        this.ipAddress = split[1];
        this.request = split[2];
        this.statusCode = Integer.valueOf(split[3]);
        this.userAgent = split[4];
    }

    public String getRequest() {
        return request;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessLogEntry that = (AccessLogEntry) o;

        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;
        return userAgent != null ? userAgent.equals(that.userAgent) : that.userAgent == null;
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccessLogEntry{" +
                "time=" + time +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
