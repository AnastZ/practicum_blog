package ru.yandex.practicum.controllers.dto;

import java.util.List;

public record UpdatingPostDTO(Long id, String title, String text, List<String> tags) implements InputPostDTO {

    @Override
    public Long getId() {
        return id;
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
