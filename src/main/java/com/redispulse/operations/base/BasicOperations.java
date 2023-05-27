package com.redispulse.operations.base;

public interface BasicOperations<T> {
    T get();
    void set(T value);
    void remove();
}
