package ru.practicum.blog.controllers;

import java.util.Optional;

public interface DTOToEntityMapper<T, DTO> {
    T toEntity(DTO dto);
}
