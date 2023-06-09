package com.redispulse.util;

import redis.clients.jedis.Jedis;

public class RedisConnection {
    protected Jedis jedis;
    protected String key;

    public RedisConnection(String key, Jedis jedis) {
        this.key = key;
        this.jedis = jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Jedis getJedis() {
        return jedis;
    }
}
