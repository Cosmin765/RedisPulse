package com.redispulse.controller.keyhandler;

import com.redispulse.operations.ListOperations;
import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.KeyData;

import java.util.ArrayList;
import java.util.List;

public class ListKeyHandler extends KeyHandler {
    private final OrderedIterableOperations<String> operations;
    public ListKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new ListOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        System.out.println(keyData.name());

        List<String> items = new ArrayList<>();
        for(int i = 0; i < 1000; ++i) {
            items.add(Integer.toString(i));
        }
        operations.assign(items);
        System.out.println("saved");
        long index = 0;
        long start = System.nanoTime();
        for(String item : operations.read()) {
            index++;
        }
        long end = System.nanoTime();

        long delta = end - start;
        System.out.println((float)delta / 1_000_000);

        System.out.println(index);
    }
}
