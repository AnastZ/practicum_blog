package ru.yandex.practicum.controllers;

/**
 * Преобразование транспортировочного объекта в сущность.
 *
 * @param <T>   сущность.
 * @param <DTO> транспортировочный объект.
 */
public interface DTOToEntityMapper<T, DTO> {
    T toEntity(DTO dto);
}
