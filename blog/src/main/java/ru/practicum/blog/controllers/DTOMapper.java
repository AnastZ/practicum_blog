package ru.practicum.blog.controllers;

import ru.yandex.practicum.model.dto.PostDTO;

import java.util.Optional;

public interface DTOMapper<T, DTO> extends EntityToDTOMapper<T, DTO>, DTOToEntityMapper<T, DTO> {
}
