package ru.yandex.practicum.services.util;

public interface Merger<T>{
    T merge(T template, T updated);
}
