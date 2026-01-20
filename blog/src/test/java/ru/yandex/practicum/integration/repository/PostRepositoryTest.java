package integration.repository;


import integration.AbstractRepositoryTest;
import integration.PostIdGenerator;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostRepositoryTest extends AbstractRepositoryTest implements PostIdGenerator {

    @Autowired
    private PostRepository postRepository;

    @ParameterizedTest
    @CsvSource({
            "11, 1, 1",
            "1111, 2, 1"
    })
    void findAllByTitle_success(final String title,
                                final int pageNumber,
                                final int pageSize) throws Exception {
        final List<Post> result = postRepository.findAllByStringQuery(title, pageNumber, pageSize);
        assertNotNull(result);
        assertTrue(result.size() <= pageSize);
        assertTrue(result.stream().anyMatch(p->p.getTitle().contains(title)));
    }
    @ParameterizedTest
    @CsvSource({
            "-1, 1",
            "2, -1"
    })
    void findAllByTitle_error(final int pageNumber, final int pageSize) throws Exception {
        assertThrows(IllegalArgumentException.class, ()->postRepository.findAllByStringQuery("", pageNumber, pageSize));
    }
    @ParameterizedTest
    @MethodSource("existingPostIds")
    void findById_success(final Long id) throws Exception {
        final Post p = postRepository.findById(id);
        assertNotNull(p);
        assertEquals(id, p.getId());
    }
    @ParameterizedTest
    @MethodSource("notExistingPostIds")
    void findById_error_notFound(final Long id) throws Exception {
        assertThrows(NoResultException.class, ()->postRepository.findById(id));
    }
    @Test
    void save_success() throws Exception {
        final Post toSave = new Post("t", "test", List.of(new Tag("t1")));
        final Post saved = postRepository.save(toSave);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals(toSave.getTitle(), saved.getTitle());
        assertEquals(toSave.getText(), saved.getText());
        assertTrue(saved.getTags().stream().noneMatch(t->t.getId() == null));
    }
    @ParameterizedTest
    @MethodSource("existingPostIds")
    void deletePostById_success(final Long postId) throws Exception {
        postRepository.delete(postId);
        assertThrows(NoResultException.class, ()->postRepository.findById(postId));
    }
    @ParameterizedTest
    @MethodSource("notExistingPostIds")
    void deletePost_error_notFound(final Long postId) {
        assertThrows(NoResultException.class, ()->postRepository.delete(postId));
    }
    @ParameterizedTest
    @MethodSource("existingPostIds")
    void incrementLikes_success(final Long postId) throws Exception {
        final Post p =  postRepository.findById(postId);
        assertEquals(postRepository.incrementLikes(postId), p.getLikesCount() + 1);
    }
    @ParameterizedTest
    @MethodSource("notExistingPostIds")
    void incrementLikes_error_notFoundPost(final Long postId) throws Exception {
        assertThrows(NoResultException.class, ()->postRepository.incrementLikes(postId));
    }
    @ParameterizedTest
    @MethodSource("notExistingPostIds")
    void updateImage_error(final Long postId) throws Exception {
        assertThrows(NoResultException.class, ()->postRepository.updatePostImagePath(postId, ""));
    }

    @ParameterizedTest
    @MethodSource("existingPostIds")
    void updateImage_success(final Long postId) throws Exception {
        final String imagePath = "pathToImage";
        postRepository.updatePostImagePath(postId, imagePath);
        assertEquals(imagePath, postRepository.findById(postId).getImagePath());
    }
}
