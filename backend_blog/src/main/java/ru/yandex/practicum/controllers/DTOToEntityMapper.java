package ru.yandex.practicum.controllers;

public interface DTOToEntityMapper<T, DTO> {
    T toEntity(DTO dto);
}
