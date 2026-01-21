package ru.yandex.practicum.controllers;

/**
 * Интерфейс объединяющий методы двух других: по преобразованию объекта в DTO и обратно.
 *
 * @param <T>   сущность.
 * @param <DTO> транспортировочный объект.
 */
public interface DTOMapper<T, DTO> extends EntityToDTOMapper<T, DTO>, DTOToEntityMapper<T, DTO> {
}
