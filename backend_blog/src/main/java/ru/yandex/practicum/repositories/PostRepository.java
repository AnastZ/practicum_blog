package ru.yandex.practicum.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p.id FROM Post p WHERE p.title LIKE CONCAT('%', :title, '%') ORDER BY p.createdDate DESC")
    Page<Long> findIdsByTitle(@NotNull final String title, @NotNull final Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id IN (:ids) ORDER BY p.createdDate DESC ")
    List<Post> findAllByIds(@NotNull final List<Long> ids);

    @Query("SELECT p.imagePath FROM Post p WHERE p.id = :id")
    Optional<String> findImagePathByPostId(@NotNull final Long id);
}
