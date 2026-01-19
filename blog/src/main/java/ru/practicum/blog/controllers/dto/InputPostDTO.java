package ru.practicum.blog.controllers.dto;

import java.util.List;

public interface InputPostDTO {
    Long getId();
    String getTitle();
    String getText();
    List<String> getTags();
}
