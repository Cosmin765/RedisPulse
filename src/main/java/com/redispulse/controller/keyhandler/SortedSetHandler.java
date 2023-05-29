package com.redispulse.controller.keyhandler;

import com.redispulse.operations.SortedSetOperations;
import com.redispulse.util.KeyData;
import javafx.util.Pair;

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
        List<Pair<Double, String>> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(new Pair<>((double)i, Integer.toString(i)));
        }
        sortedSetOperations.assign(items);

        for(Pair<Double, String> item : sortedSetOperations.getRange(23, 51)) {
            System.out.println(item);
        }
    }
}
