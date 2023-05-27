package com.redispulse.util;

import redis.clients.jedis.Jedis;

import java.io.Serializable;

public record KeyData(String name, KeyType type, Jedis connection) implements Serializable {}
