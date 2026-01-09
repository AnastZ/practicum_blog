package ru.yandex.practicum.model;

import ru.yandex.practicum.model.dto.PostDTO;

import java.util.Optional;

public interface DTOMapper<T, DTO> {
    Optional<T> toEntity(DTO dto);
    Optional<DTO> toDTO(T entity);
}
