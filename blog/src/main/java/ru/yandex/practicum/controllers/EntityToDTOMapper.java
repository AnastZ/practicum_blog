package ru.yandex.practicum.controllers;

public interface EntityToDTOMapper<T, DTO> {
    DTO toDTO(T entity);
}
