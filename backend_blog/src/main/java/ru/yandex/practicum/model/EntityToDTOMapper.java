package ru.yandex.practicum.model;

import java.util.Optional;

public interface EntityToDTOMapper<T, DTO> {
    DTO toDTO(T entity);
}
