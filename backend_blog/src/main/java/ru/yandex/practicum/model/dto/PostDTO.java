package ru.yandex.practicum.model.dto;

import java.time.LocalDate;


public record PostDTO(Long id,
                      String title,
                      String text,
                      Long likesCount,
                      Long countComments) {
}
