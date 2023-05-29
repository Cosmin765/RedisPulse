package com.redispulse.operations;

import com.redispulse.operations.base.BasicOperations;
import com.redispulse.util.RedisConnection;
import redis.clients.jedis.Jedis;

public class StringOperations
        extends RedisConnection
        implements BasicOperations<String> {
    public StringOperations(String key, Jedis jedis) {
        super(key, jedis);
    }

    @Override
    public String read() {
        return jedis.get(key);
    }

    @Override
    public void assign(String value) {
        jedis.set(key, value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }
}
