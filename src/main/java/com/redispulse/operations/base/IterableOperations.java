package com.redispulse.operations.base;

public interface IterableOperations<T> extends BasicOperations<Iterable<T>> {
    Iterable<T> getRange(long start, long end);
    void push(T item);
    T pop();
    void pushMultiple(Iterable<T> items);
}
