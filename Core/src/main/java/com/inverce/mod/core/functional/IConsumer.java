package com.inverce.mod.core.functional;

@FunctionalInterface
public interface IConsumer<T> {
    void consume(T element);
}
