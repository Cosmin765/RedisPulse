package com.redispulse.util;

public enum KeyType {
    STRING,
    DICTIONARY,
    ZSET,
    LIST,
    SET;

    public static KeyType fromString(String type) {
        switch (type) {
            case "string" -> {
                return KeyType.STRING;
            }
            case "hash" -> {
                return KeyType.DICTIONARY;
            }
            case "zset" -> {
                return KeyType.ZSET;
            }
            case "set" -> {
                return KeyType.SET;
            }
            case "list" -> {
                return KeyType.LIST;
            }
        }
        throw new RuntimeException("Type " + type + " not handled properly");
    }
}
