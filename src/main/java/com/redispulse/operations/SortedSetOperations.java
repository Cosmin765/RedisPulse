package com.redispulse.operations;

import com.redispulse.operations.base.BufferedOperations;
import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.RedisConnection;
import com.redispulse.util.RedisIterable;
import javafx.util.Pair;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortedSetOperations
    extends RedisConnection
    implements OrderedIterableOperations<Pair<Double, String>>, BufferedOperations<Pair<Double, String>> {

    private List<Pair<Double, String>> buffer = new ArrayList<>();
    private int iterationCount = 0;
    private long start;
    private long end;
    private int actualIndex = 0;

    @Override
    public Iterable<Pair<Double, String>> get() {
        return getRange(0, -1);
    }

    @Override
    public void set(Iterable<Pair<Double, String>> value) {
        remove();
        pushMultiple(value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }

    @Override
    public Iterable<Pair<Double, String>> getRange(long start, long end) {
        this.start = start;
        this.end = end == -1 ? Long.MAX_VALUE : end - start;
        iterationCount = 0;
        retrieveItems();
        return new RedisIterable<>(this);
    }

    @Override
    public void push(Pair<Double, String> item) {
        jedis.zadd(key, item.getKey(), item.getValue());
    }

    @Override
    public Pair<Double, String> pop() {
        Tuple item = jedis.zpopmax(key);
        return new Pair<>(item.getScore(), item.getElement());
    }

    @Override
    public void pushMultiple(Iterable<Pair<Double, String>> items) {
        Map<String, Double> buffer = new HashMap<>();

        for(Pair<Double, String> pair : items) {
            if(buffer.size() >= BUFFER_SIZE) {
                pushMultiple(buffer);
                buffer.clear();
            }
            buffer.put(pair.getValue(), pair.getKey());
        }

        if(buffer.size() > 0) {
            pushMultiple(buffer);
        }
    }

    public void pushMultiple(Map<String, Double> items) {
        jedis.zadd(key, items);
    }

    @Override
    public void pushBack(Pair<Double, String> item) {
        push(item);
    }

    @Override
    public Pair<Double, String> popBack() {
        Tuple item = jedis.zpopmin(key);
        return new Pair<>(item.getScore(), item.getElement());
    }

    @Override
    public Pair<Double, String> nextSupplier(long index) {
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
        buffer = jedis.zrangeWithScores(key, localStart, localEnd).stream().map(t ->
                new Pair<>(t.getScore(), t.getElement())).toList();
        actualIndex = 0;
    }
}
