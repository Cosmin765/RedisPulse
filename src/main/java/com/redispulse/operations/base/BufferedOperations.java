package com.redispulse.operations.base;

public interface BufferedOperations<T> {
    int BUFFER_SIZE = 10_000;
    T nextSupplier(long index);
    void retrieveItems();
}
