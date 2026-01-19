package integration.repository;

import integration.AbstractRepositoryTest;
import integration.TagNamesGenerator;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.repository.TagRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TagRepositoryTest extends AbstractRepositoryTest implements TagNamesGenerator {

    @Autowired
    private TagRepository tagRepository;



    @ParameterizedTest
    @MethodSource("existingTagNames")
    void findTags_success(final List<String> tags) throws Exception {
        assertTrue(tagRepository.findByNames(tags)
                .stream()
                .allMatch(t->t.getId() != null && t.getId() > 0 && tags.contains(t.getName())));

    }
    @ParameterizedTest
    @MethodSource("notExistingTagNames")
    void findTags_notExisting_listEmpty(final List<String> tags) throws Exception {
        assertTrue(tagRepository.findByNames(tags).isEmpty());
    }
    @ParameterizedTest
    @MethodSource("existingTagNames")
    void saveExistingTags(final List<String> tags) throws Exception {
        final List<Tag> existingTags = tagRepository.findByNames(tags);
        tagRepository.saveAll(existingTags);
    }
    @ParameterizedTest
    @MethodSource("existingTagNames")
    void saveExistingTags_error(final List<String> tags) throws Exception {
        final List<Tag> existingTags = tags.stream().map(n->new Tag(n)).toList();
        assertThrows(ConstraintViolationException.class, ()->tagRepository.saveAll(existingTags));
    }
    @ParameterizedTest
    @MethodSource("notExistingTagNames")
    void saveNotExistingTags(final List<String> tags) throws Exception {
        final List<Tag> existingTags = tags.stream().map(n->new Tag(n)).toList();
        tagRepository.saveAll(existingTags);
    }
}
