package com.redispulse.controller.keyhandler;

import com.redispulse.operations.ListOperations;
import com.redispulse.util.KeyData;

import java.util.ArrayList;
import java.util.List;

public class ListKeyHandler extends KeyHandler {
    private final ListOperations listOperations = new ListOperations();
    public ListKeyHandler(KeyData keyData) {
        super(keyData);
        listOperations.setKey(keyData.name());
        listOperations.setJedis(keyData.connection());
    }

    @Override
    public void handleSelect() {
        System.out.println(keyData.name());

        List<String> items = new ArrayList<>();
        for(int i = 0; i < 1000; ++i) {
            items.add(Integer.toString(i));
        }
        listOperations.assign(items);
        System.out.println("saved");
        long index = 0;
        long start = System.nanoTime();
        for(String item : listOperations.read()) {
            index++;
        }
        long end = System.nanoTime();

        long delta = end - start;
        System.out.println((float)delta / 1_000_000);

        System.out.println(index);
    }
}
