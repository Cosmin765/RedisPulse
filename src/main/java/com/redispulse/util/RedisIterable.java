package com.redispulse.util;

import java.util.Iterator;
import java.util.function.Function;

public class RedisIterable<T> implements Iterable<T> {
    private final Function<Long, T> nextSupplier;
    private boolean updated = false;
    private T nextItem = null;
    private long index = 0;
    public RedisIterable(Function<Long, T> nextSupplier) {
        this.nextSupplier = nextSupplier;
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
                nextItem = nextSupplier.apply(index++);
            }
            return nextItem != null;
        }

        @Override
        public T next() {
            if(!updated) {
                return nextSupplier.apply(index++);
            }
            updated = false;
            return nextItem;
        }
    }
}
