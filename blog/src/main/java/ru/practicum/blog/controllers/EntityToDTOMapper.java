package ru.practicum.blog.controllers;

import java.util.Optional;

public interface EntityToDTOMapper<T, DTO> {
    DTO toDTO(T entity);
}
