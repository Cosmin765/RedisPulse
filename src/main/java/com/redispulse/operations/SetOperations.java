package com.redispulse.operations;

import com.redispulse.operations.base.BufferedOperations;
import com.redispulse.operations.base.IterableOperations;
import com.redispulse.util.RedisConnection;
import com.redispulse.util.RedisIterable;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class SetOperations
        extends RedisConnection
        implements IterableOperations<String>, BufferedOperations<String> {
    private List<String> buffer = new ArrayList<>();
    private long start;
    private long end;
    private boolean started = false;
    private int actualIndex = 0;
    private String cursor = ScanParams.SCAN_POINTER_START;
    @Override
    public Iterable<String> get() {
        return getRange(0, -1);
    }

    @Override
    public void set(Iterable<String> value) {
        remove();
        pushMultiple(value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }

    @Override
    public String nextSupplier(long index) {
        if(index > end) {
            return null;
        }
        if(actualIndex >= buffer.size()) {
            if(cursor.equals("0")) {
                return null;
            } else {
                retrieveItems();
                return nextSupplier(index);
            }
        }
        return buffer.get(actualIndex++);
    }

    @Override
    public void retrieveItems() {
        if(started && cursor.equals("0")) {
            return;
        }
        started = true;
        ScanParams scanParams = new ScanParams().count(BUFFER_SIZE);
        ScanResult<String> scanResult = jedis.sscan(key, cursor, scanParams);

        cursor = scanResult.getCursor();

        List<String> result = scanResult.getResult();

        if(start > 0) {
            // skip elements
            if(result.size() <= start) {
                start -= result.size();
                retrieveItems();
                return;
            }

            result = result.subList((int)start, result.size());
            start = 0;
        }

        buffer = result;
        actualIndex = 0;
    }

    @Override
    public Iterable<String> getRange(long start, long end) {
        this.started = false;
        this.start = start;
        this.end = end == -1 ? Long.MAX_VALUE : end - start;
        retrieveItems();
        return new RedisIterable<>(this);
    }

    @Override
    public void push(String item) {
        jedis.sadd(key, item);
    }

    @Override
    public String pop() {
        return jedis.spop(key);
    }

    @Override
    public void pushMultiple(Iterable<String> items) {
        jedis.sadd(key, StreamSupport.stream(items.spliterator(), false).toArray(String[]::new));
    }
}
