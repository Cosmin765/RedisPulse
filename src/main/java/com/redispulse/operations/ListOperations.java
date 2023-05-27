package com.redispulse.operations;

import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.RedisConnection;
import com.redispulse.util.RedisIterable;

import java.util.List;
import java.util.stream.StreamSupport;

public class ListOperations extends RedisConnection implements OrderedIterableOperations<String> {
    private List<String> buffer;
    private int iterationCount = 0;
    private long start;
    private long end;

    @Override
    public Iterable<String> get() {
        return getRange(0, -1);
    }

    @Override
    public void set(Iterable<String> items) {
        remove();
        pushMultiple(items);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }

    @Override
    public Iterable<String> getRange(long start, long end) {
        this.start = start;
        this.end = end;
        iterationCount = 0;
        retrieveItems();
        return new RedisIterable<>(this::nextSupplier);
    }

    @Override
    public void push(String item) {
        jedis.rpush(key, item);
    }

    @Override
    public String pop() {
        return jedis.lpop(key);
    }

    @Override
    public void pushMultiple(Iterable<String> items) {
        jedis.rpush(key, StreamSupport.stream(items.spliterator(), false).toArray(String[]::new));
    }

    @Override
    public void pushBack(String item) {
        jedis.rpush(key, item);
    }

    @Override
    public String popBack() {
        return jedis.rpop(key);
    }

    private void retrieveItems() {
        long localStart = start + (long) iterationCount * BUFFER_SIZE;
        long localEnd = start + (long) (iterationCount + 1) * BUFFER_SIZE;

        buffer = jedis.lrange(key, localStart, localEnd);
    }

    private String nextSupplier(long index) {
        if(start + index > (long) BUFFER_SIZE * (iterationCount + 1)) {
            iterationCount++;
            retrieveItems();
        }

        int actualIndex = (int) (index % BUFFER_SIZE);
        return actualIndex < buffer.size() ? buffer.get(actualIndex) : null;
    }
}
