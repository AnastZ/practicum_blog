package ru.yandex.practicum.integration.repository;


import jakarta.persistence.NoResultException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.integration.AbstractRepositoryTest;
import ru.yandex.practicum.integration.CommentGenerator;
import ru.yandex.practicum.integration.PostIdGenerator;
import ru.yandex.practicum.repositories.CommentRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CommentRepositoryTest extends AbstractRepositoryTest implements PostIdGenerator, CommentGenerator {
/*    @Autowired
    private CommentRepository commentRepository;


    @ParameterizedTest
    @MethodSource("existingPostIds")
    void allCommentsByPostId_success(final Long postId) throws Exception {
        assertFalse(commentRepository.findByPostId(postId).isEmpty());
    }
    @ParameterizedTest
    @MethodSource("notExistingPostIds")
    void allCommentsByPostId_postNotExisting(final Long postId) throws Exception {
        assertTrue(commentRepository.findByPostId(postId).isEmpty());
    }
    @ParameterizedTest
    @MethodSource("existingPostAndComments")
    void getCommitByIdAndPostId_success(final Long postId, final Long commentId) throws Exception {
        assertNotNull(commentRepository.findByIdAndPost(commentId, postId));
    }
    @ParameterizedTest
    @MethodSource("notCorrespondingPostAndComment")
    void getCommitByIdAndPostId_error(final Long postId, final Long commentId) throws Exception {
        assertThrows(NoResultException.class, ()->commentRepository.findByIdAndPost(commentId, postId));
    }*/

}
