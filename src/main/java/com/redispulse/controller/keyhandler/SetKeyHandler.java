package com.redispulse.controller.keyhandler;

import com.redispulse.operations.SetOperations;
import com.redispulse.operations.base.IterableOperations;
import com.redispulse.util.KeyData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetKeyHandler extends KeyHandler {
    private final IterableOperations<String> operations;

    public SetKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new SetOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        List<String> items = new ArrayList<>();
        for(int i = 0; i < 20_000; ++i) {
            items.add(Integer.toString(i));
        }
        operations.assign(items);

        Set<String> seen = new HashSet<>();

        long index = 0;
        for(String item : operations.getRange(0, -1)) {
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
