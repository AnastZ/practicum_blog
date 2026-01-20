package ru.practicum.blog.controllers.dto;

import jakarta.validation.constraints.NotNull;


import java.util.Collections;
import java.util.List;


public record PostDTO(Long id,
                      String title,
                      String text,
                      List<String> tags,
                      Long likesCount,
                      Long commentsCount){

    public PostDTO(@NotNull final Long id,
                   @NotNull final String title,
                   @NotNull final String text,
                   @NotNull final List<String> tags,
                   @NotNull final Long likesCount,
                   @NotNull final Long commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = Collections.unmodifiableList(tags);
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
    /**
     * Получить неизменяемый массив наименований тегов.
     * @return unmodifiableList наименований тегов.
     */
    public @NotNull List<String> getTags() {
        return tags;
    }
}
