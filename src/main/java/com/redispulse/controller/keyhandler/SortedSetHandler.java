package com.redispulse.controller.keyhandler;

import com.redispulse.operations.SortedSetOperations;
import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.KeyData;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SortedSetHandler extends KeyHandler {
    private final OrderedIterableOperations<Tuple> operations;
    public SortedSetHandler(KeyData keyData) {
        super(keyData);
        operations = new SortedSetOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<Tuple> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(new Tuple(Integer.toString(i), (double)i));
        }
        operations.assign(items);

        for(Tuple item : operations.getRange(23, 51)) {
            System.out.println(item);
        }
    }
}
