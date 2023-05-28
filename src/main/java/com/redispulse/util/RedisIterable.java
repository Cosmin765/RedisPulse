package com.redispulse.util;

import com.redispulse.operations.base.BufferedOperations;

import java.util.Iterator;

public class RedisIterable<T> implements Iterable<T> {
    private final BufferedOperations<T> operations;
    private boolean updated = false;
    private T nextItem = null;
    private long index = 0;
    public RedisIterable(BufferedOperations<T> operations) {
        this.operations = operations;
    }
    @Override
    public Iterator<T> iterator() {
        return new RedisIterator();
    }

    private class RedisIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            if(!updated) {
                updated = true;
                nextItem = operations.nextSupplier(index++);
            }
            return nextItem != null;
        }

        @Override
        public T next() {
            if(!updated) {
                return operations.nextSupplier(index++);
            }
            updated = false;
            return nextItem;
        }
    }
}
