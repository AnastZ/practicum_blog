package ru.yandex.practicum.controllers.dto;


import java.util.List;

public record AddingPostDTO(String title, String text, List<String> tags) implements InputPostDTO {
    @Override
    public Long getId() {
        return 0L;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }
}
