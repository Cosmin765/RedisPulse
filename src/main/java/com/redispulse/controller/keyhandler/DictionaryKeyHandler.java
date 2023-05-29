package com.redispulse.controller.keyhandler;

import com.redispulse.operations.DictionaryOperations;
import com.redispulse.util.KeyData;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DictionaryKeyHandler extends KeyHandler {
    private final DictionaryOperations dictionaryOperations = new DictionaryOperations();
    public DictionaryKeyHandler(KeyData keyData) {
        super(keyData);
        dictionaryOperations.setKey(keyData.name());
        dictionaryOperations.setJedis(keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<Pair<String, String>> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(new Pair<>(Integer.toString(i), Integer.toString(i)));
        }
        dictionaryOperations.assign(items);

        System.out.println("-------------------");

        Set<Pair<String, String>> seen = new HashSet<>();

        long index = 0;
        for(Pair<String, String> item : dictionaryOperations.read()) {
            if(seen.contains(item)) {
                throw new RuntimeException();
            }
            seen.add(item);
//            System.out.println(item);
            index++;
        }
        System.out.println(index);
    }
}
