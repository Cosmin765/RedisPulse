package com.redispulse.util;

import java.util.Iterator;

public class RedisIterable<T> implements Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        return new RedisIterator();
    }

    private class RedisIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    }
}
