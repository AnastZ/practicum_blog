package ru.yandex.practicum.controllers;

/**
 * Преобразование сущности в транспортировочный объект.
 *
 * @param <T>   сущность.
 * @param <DTO> транспортировочный объект.
 */
public interface EntityToDTOMapper<T, DTO> {
    DTO toDTO(T entity);
}
