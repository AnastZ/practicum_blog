package ru.yandex.practicum.controllers;


public interface DTOMapper<T, DTO> extends EntityToDTOMapper<T, DTO>, DTOToEntityMapper<T, DTO> {
}
