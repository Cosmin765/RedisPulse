package com.redispulse.controller.keyhandler;

import com.redispulse.operations.DictionaryOperations;
import com.redispulse.operations.base.IterableOperations;
import com.redispulse.util.KeyData;

import java.util.*;

public class DictionaryKeyHandler extends KeyHandler {
    private final IterableOperations<Map.Entry<String, String>> operations;
    public DictionaryKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new DictionaryOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
//        List<Map.Entry<String, String>> items = new ArrayList<>();
//        for(int i = 0; i < 20_000; ++i) {
//            Map.Entry<String, String> item = new AbstractMap.SimpleEntry<>(Integer.toString(i), Integer.toString(i));
//            items.add(item);
//        }
//        operations.assign(items);
//
//        System.out.println("-------------------");

//        Set<Map.Entry<String, String>> seen = new HashSet<>();

        long index = 0;
        for(Map.Entry<String, String> item : operations.read()) {
//            if(seen.contains(item)) {
//                throw new RuntimeException();
//            }
//            seen.add(item);
//            System.out.println(item);
            index++;
        }
        System.out.println(index);
    }
}
