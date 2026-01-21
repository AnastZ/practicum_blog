package ru.yandex.practicum.integration.services;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.integration.TagNamesGenerator;
import ru.yandex.practicum.models.Tag;
import ru.yandex.practicum.services.TagService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TagServiceTest implements TagNamesGenerator {

    @Autowired
    private TagService tagService;

    @ParameterizedTest
    @MethodSource("tagNamesMix")
    void saveAndGet_success(final List<String> names) throws Exception {
        final List<String> cleanNames = tagService.getClearNames(names);
        final List<Tag> tags = tagService.saveTagsAndGet(names);
        assertEquals(tags.size(), cleanNames.size());
        assertTrue(tags.stream().allMatch(tag -> cleanNames.contains(tag.getName())));
    }

}
