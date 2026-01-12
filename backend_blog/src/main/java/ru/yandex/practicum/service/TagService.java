package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.repository.TagRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TagService {

    private final Logger logger = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(@NotNull final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public @NotNull List<Tag> findByNames(@NotNull final List<String> names) throws Exception {
        final List<String> cleanNames = names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct() // Оставляем только уникальные названия
                .toList();
        if (cleanNames.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Tag> existingTags = tagRepository.findByNames(cleanNames);
        final List<String> existingNames = existingTags.stream()
                .map(t -> t.getName().toLowerCase())
                .toList();
        final List<Tag> newTagsToSave = cleanNames.stream()
                .filter(name -> !existingNames.contains(name.toLowerCase()))
                .map(Tag::new)
                .toList();

        List<Tag> savedNewTags = Collections.emptyList();
        if (!newTagsToSave.isEmpty()) {
            savedNewTags = tagRepository.saveAll(newTagsToSave);
        }

        return Stream.concat(existingTags.stream(), savedNewTags.stream()).toList();

    }
}
