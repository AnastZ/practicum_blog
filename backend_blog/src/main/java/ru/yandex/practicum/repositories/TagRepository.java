package ru.yandex.practicum.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.models.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    /**
     * Найти тэги по наименованию.
     *
     * @param names наименования тегов.
     * @return список найденных тэгов.
     */
    @Query("SELECT t FROM Tag t WHERE t.name IN (:names)")
    List<Tag> findByNames(@NotNull final List<String> names);
}
