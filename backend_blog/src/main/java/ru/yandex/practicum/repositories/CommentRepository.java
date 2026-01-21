package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Получить список комментариев по id поста.
     *
     * @param postId уникальный номер поста.
     * @return список комментариев поста.
     */
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(@Param("postId") final Long postId);

    /**
     * Найти комментарий по id, относящийся к посту.
     *
     * @param commentId уникальный номер комментария.
     * @param postId    уникальный номер поста.
     * @return комментарий поста.
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.post.id = :postId")
    Optional<Comment> findByIdAndPost(@Param("commentId") final Long commentId,
                                      @Param("postId") final Long postId);

}

