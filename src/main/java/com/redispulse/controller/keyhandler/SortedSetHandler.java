package com.redispulse.controller.keyhandler;

import com.redispulse.operations.SortedSetOperations;
import com.redispulse.util.KeyData;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SortedSetHandler extends KeyHandler {
    private final SortedSetOperations sortedSetOperations = new SortedSetOperations();
    public SortedSetHandler(KeyData keyData) {
        super(keyData);
        sortedSetOperations.setKey(keyData.name());
        sortedSetOperations.setJedis(keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<Tuple> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(new Tuple(Integer.toString(i), (double)i));
        }
        sortedSetOperations.assign(items);

        for(Tuple item : sortedSetOperations.getRange(23, 51)) {
            System.out.println(item);
        }
    }
}
