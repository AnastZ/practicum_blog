package ru.practicum.blog.services.util;

public interface Merger<T>{
    T merge(T template, T updated);
}
