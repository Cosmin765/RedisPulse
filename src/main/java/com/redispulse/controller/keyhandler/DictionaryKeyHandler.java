package com.redispulse.controller.keyhandler;

import com.redispulse.operations.DictionaryOperations;
import com.redispulse.util.KeyData;

import java.util.*;

public class DictionaryKeyHandler extends KeyHandler {
    private final DictionaryOperations dictionaryOperations = new DictionaryOperations();
    public DictionaryKeyHandler(KeyData keyData) {
        super(keyData);
        dictionaryOperations.setKey(keyData.name());
        dictionaryOperations.setJedis(keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<Map.Entry<String, String>> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            Map.Entry<String, String> item = new AbstractMap.SimpleEntry<>(Integer.toString(i), Integer.toString(i));
            items.add(item);
        }
        dictionaryOperations.assign(items);

        System.out.println("-------------------");

//        Set<Map.Entry<String, String>> seen = new HashSet<>();

        long index = 0;
        for(Map.Entry<String, String> item : dictionaryOperations.read()) {
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
