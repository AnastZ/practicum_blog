package ru.yandex.practicum.integration.service;

import integration.AbstractRepositoryTest;
import integration.TagNamesGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.service.TagService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagServiceTest extends AbstractRepositoryTest implements TagNamesGenerator {

    @Autowired
    private TagService tagService;

    @ParameterizedTest
    @MethodSource("tagNamesMix")
    void saveAndGet_success(final List<String> names) throws Exception {
        final List<Tag> tags = tagService.saveTagsAndGet(names);
        tags.forEach(System.out::println);
        assertEquals(tags.size(), names.size());
    }
}
