package com.redispulse.operations;

import com.redispulse.operations.base.BufferedOperations;
import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.RedisConnection;
import com.redispulse.util.RedisIterable;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortedSetOperations
    extends RedisConnection
    implements OrderedIterableOperations<Tuple>, BufferedOperations<Tuple> {

    private List<Tuple> buffer = new ArrayList<>();
    private int iterationCount = 0;
    private long start;
    private long end;
    private int actualIndex = 0;

    @Override
    public Iterable<Tuple> read() {
        return getRange(0, -1);
    }

    @Override
    public void assign(Iterable<Tuple> value) {
        remove();
        pushMultiple(value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }

    @Override
    public Iterable<Tuple> getRange(long start, long end) {
        this.start = start;
        this.end = end == -1 ? Long.MAX_VALUE : end - start;
        iterationCount = 0;
        retrieveItems();
        return new RedisIterable<>(this);
    }

    @Override
    public void push(Tuple item) {
        jedis.zadd(key, item.getScore(), item.getElement());
    }

    @Override
    public Tuple pop() {
        return jedis.zpopmax(key);
    }

    @Override
    public void pushMultiple(Iterable<Tuple> items) {
        Map<String, Double> buffer = new HashMap<>();

        for(Tuple pair : items) {
            if(buffer.size() >= BUFFER_SIZE) {
                pushMultiple(buffer);
                buffer.clear();
            }
            buffer.put(pair.getElement(), pair.getScore());
        }

        if(buffer.size() > 0) {
            pushMultiple(buffer);
            buffer.clear();
        }
    }

    public void pushMultiple(Map<String, Double> items) {
        jedis.zadd(key, items);
    }

    @Override
    public void pushBack(Tuple item) {
        push(item);
    }

    @Override
    public Tuple popBack() {
        return jedis.zpopmin(key);
    }

    @Override
    public Tuple nextSupplier(long index) {
        if(start + index > (long) BUFFER_SIZE * (iterationCount + 1)) {
            iterationCount++;
            retrieveItems();
        }
        return index <= end && actualIndex < buffer.size() ? buffer.get(actualIndex++) : null;
    }

    @Override
    public void retrieveItems() {
        long localStart = start + (long) iterationCount * BUFFER_SIZE;
        long localEnd = start + (long) (iterationCount + 1) * BUFFER_SIZE;
        buffer = jedis.zrangeWithScores(key, localStart, localEnd);
        actualIndex = 0;
    }
}
