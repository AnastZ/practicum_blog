package ru.yandex.practicum.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Найти посты, у которых присутствует искомое слово в наименовании.
     *
     * @param searchStr поисковой запрос.
     * @param pageable  информация о размере страниц.
     * @return результат в обёртке для работы со страницами.
     */
    @Query("SELECT p.id FROM Post p WHERE p.title LIKE CONCAT('%', :searchStr, '%') ORDER BY p.createdDate DESC")
    Page<Long> findIdsByTitle(@NotNull final String searchStr, @NotNull final Pageable pageable);

    /**
     * Получить объект по их уникальному номеру.
     *
     * @param ids уникальные номера постов.
     * @return список найденных постов.
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id IN (:ids) ORDER BY p.createdDate DESC ")
    List<Post> findAllByIds(@NotNull final List<Long> ids);

}
