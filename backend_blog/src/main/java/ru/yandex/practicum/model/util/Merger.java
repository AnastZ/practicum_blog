package ru.yandex.practicum.model.util;

public interface Merger<T>{
    T merge(T template, T updated);
}
