package ru.practicum.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.blog.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(@Param("postId") final Long postId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.post.id = :postId")
    Optional<Comment> findByIdAndPost(@Param("commentId") final Long commentId,
                                      @Param("postId") final Long postId);

}

