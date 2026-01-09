package ru.yandex.practicum.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Объект для отправки ответа на запрос постов выбранной страницы.
 * @param posts неизменяемый список постов.
 * @param hasPrev true если текущая страница не первая.
 * @param hasNext true, если текущая страница не последняя.
 * @param lastPage номер последней страницы (количество страниц всего).
 */
public record FoundPostsDTO(@NotNull List<PostDTO> posts,
                            boolean hasPrev,
                            boolean hasNext,
                            long lastPage) {


    public FoundPostsDTO(@NotNull final List<PostDTO> posts,
                         final boolean hasPrev,
                         final boolean hasNext,
                         final long lastPage) {
        this.posts = Collections.unmodifiableList(posts);
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.lastPage = lastPage;
    }

    /**
     * Получить неизменяемый массив постов.
     * @return unmodifiableList с постами.
     */
    public @NotNull List<PostDTO> getPosts() {
        return posts;
    }
    public static FoundPostsDTO getEmpty() {
        return new FoundPostsDTO(Collections.emptyList(),
                false,
                false,
                1);
    }
}
