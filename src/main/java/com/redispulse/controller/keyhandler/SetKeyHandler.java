package com.redispulse.controller.keyhandler;

import com.redispulse.operations.SetOperations;
import com.redispulse.util.KeyData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetKeyHandler extends KeyHandler {
    private final SetOperations setOperations = new SetOperations();

    public SetKeyHandler(KeyData keyData) {
        super(keyData);
        setOperations.setKey(keyData.name());
        setOperations.setJedis(keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<String> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(Integer.toString(i));
        }
        setOperations.set(items);

        Set<String> seen = new HashSet<>();

        long index = 0;
        for(String item : setOperations.getRange(0, -1)) {
            if(seen.contains(item)) {
                throw new RuntimeException();
            }
            seen.add(item);
            if(index < 10) {
//                System.out.println(item);
            }
            index++;
        }
        System.out.println(index);
    }
}
