package ru.yandex.practicum.model.dto;

import java.util.List;

public interface InputPostDTO {
    Long getId();
    String getTitle();
    String getText();
    List<String> getTags();
}
