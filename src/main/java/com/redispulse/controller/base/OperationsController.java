package com.redispulse.controller.base;

import com.redispulse.util.RedisIterable;

public interface OperationsController<T> {
    RedisIterable<T> getAll();
    RedisIterable<T> getRange(long start, long end);
    T get();
    void set();
}
