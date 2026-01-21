package ru.yandex.practicum.util;

public interface EntityValidator<T> {
    default boolean isValid(T entity){
        return true;
    }
    void validate(T entity) throws IllegalArgumentException;
}
