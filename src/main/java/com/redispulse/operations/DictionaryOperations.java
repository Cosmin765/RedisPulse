package com.redispulse.operations;

import com.redispulse.operations.base.BufferedOperations;
import com.redispulse.operations.base.IterableOperations;
import com.redispulse.util.RedisConnection;
import com.redispulse.util.RedisIterable;
import javafx.util.Pair;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryOperations
    extends RedisConnection
    implements IterableOperations<Pair<String, String>>, BufferedOperations<Pair<String, String>> {

    private List<Pair<String, String>> buffer = new ArrayList<>();
    private long start;
    private long end;
    private boolean started = false;
    private int actualIndex = 0;
    private String cursor = ScanParams.SCAN_POINTER_START;

    @Override
    public Iterable<Pair<String, String>> read() {
        return getRange(0, -1);
    }

    @Override
    public void assign(Iterable<Pair<String, String>> value) {
        remove();
        pushMultiple(value);
    }

    @Override
    public void remove() {
        jedis.del(key);
    }

    @Override
    public Iterable<Pair<String, String>> getRange(long start, long end) {
        this.started = false;
        this.start = start;
        this.end = end == -1 ? Long.MAX_VALUE : end - start;
        cursor = ScanParams.SCAN_POINTER_START;
        retrieveItems();
        return new RedisIterable<>(this);
    }

    @Override
    public void push(Pair<String, String> item) {
        jedis.hset(key, item.getKey(), item.getValue());
    }

    @Override
    public Pair<String, String> pop() {
        ScanParams scanParams = new ScanParams().count(1);
        ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(key, ScanParams.SCAN_POINTER_START, scanParams);

        List<Map.Entry<String, String>> result = scanResult.getResult();

        if(result.size() == 0) {
            return null;
        }

        Pair<String, String> item = new Pair<>(result.get(0).getKey(), result.get(0).getValue());
        jedis.hdel(key, item.getKey());
        return item;
    }

    @Override
    public void pushMultiple(Iterable<Pair<String, String>> items) {
        Map<String, String> buffer = new HashMap<>();

        for(Pair<String, String> pair : items) {
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

    public void pushMultiple(Map<String, String> items) {
        jedis.hset(key, items);
    }

    @Override
    public Pair<String, String> nextSupplier(long index) {
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
        ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(key, cursor, scanParams);

        cursor = scanResult.getCursor();
        List<Map.Entry<String, String>> result = scanResult.getResult();

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

        buffer = result.stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toList();
        actualIndex = 0;
    }
}
