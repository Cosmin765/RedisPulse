package com.redispulse.operations;

import com.redispulse.operations.base.BasicOperations;
import com.redispulse.util.RedisConnection;

public class StringOperations extends RedisConnection implements BasicOperations<String> {
    @Override
    public String get() {
        return jedis.get(key);
    }

    @Override
    public void set(String value) {
        jedis.set(key, value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }
}
