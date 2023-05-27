package com.redispulse.operations.base;

public interface OrderedIterableOperations<T> extends IterableOperations<T> {
    void pushBack(T item);
    T popBack();
}
