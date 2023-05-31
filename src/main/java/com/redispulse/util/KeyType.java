package com.redispulse.util;

import java.util.Locale;

public enum KeyType {
    STRING,
    DICTIONARY,
    ZSET,
    LIST,
    SET;

    public static KeyType fromString(String type) {
        switch (type.toLowerCase(Locale.ROOT)) {
            case "string" -> {
                return KeyType.STRING;
            }
            case "hash", "dictionary" -> {
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
