package ru.yandex.practicum.model.dto;


import java.util.List;

public record AddingPostDTO(String title, String text, List<String> tags) {
}
