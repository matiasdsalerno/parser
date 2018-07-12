package com.ef.model;

public class BlockedIpAddress {

    private String key;
    private String message;

    public BlockedIpAddress(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BlockedIpAddress{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
