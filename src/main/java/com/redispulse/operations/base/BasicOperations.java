package com.redispulse.operations.base;

public interface BasicOperations<T> {
    T read();
    void assign(T value);
    void remove();
}
