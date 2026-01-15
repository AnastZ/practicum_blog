package integration.repository;


import integration.AbstractRepositoryTest;
import integration.PostIdGenerator;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import ru.yandex.practicum.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;

public class PostRepositoryTest extends AbstractRepositoryTest implements PostIdGenerator {

    @Autowired
    private PostRepository postRepository;

    @Rollback(false)
    @ParameterizedTest
    @MethodSource("testExistingPostIds")
    void deletePostById_success(final Long postId) throws Exception {
        postRepository.delete(postId);
        assertThrows(NoResultException.class, ()->postRepository.findById(postId));
    }


}
