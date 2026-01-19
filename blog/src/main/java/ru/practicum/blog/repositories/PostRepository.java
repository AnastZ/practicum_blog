package ru.practicum.blog.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.blog.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT count(p) FROM Post p WHERE p.title LIKE CONCAT('%', :title, '%') ORDER BY p.createdDate DESC")
    List<Long> findIdsByTitle(@NotNull final String title);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id IN (:ids) ORDER BY p.createdDate DESC ")
    List<Post> findAllByIds(@NotNull final List<Long> ids);

    Optional<Post> findById(@NotNull final Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.id = :id")
    void incrementLikesForPostId(@NotNull final Long id);

    @Query("SELECT p.likesCount FROM Post p WHERE p.id = :id")
    Long getCountLikesByPostId(@NotNull final Long id);

    @Query("SELECT p.imagePath FROM Post p WHERE p.id = :id")
    Optional<String> findImagePathByPostId(@NotNull final Long id);
}
