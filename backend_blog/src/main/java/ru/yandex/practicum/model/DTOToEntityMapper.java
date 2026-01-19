package ru.yandex.practicum.model;

import java.util.Optional;

public interface DTOToEntityMapper<T, DTO> {
    T toEntity(DTO dto);
}
