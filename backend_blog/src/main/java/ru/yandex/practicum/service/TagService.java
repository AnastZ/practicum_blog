package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.repository.TagRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TagService {

    private final TagRepository tagRepository;
    public TagService(@NotNull final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public @NotNull List<Tag> findByNames(@NotNull final List<String> names) {
        final List<Tag> existingTags = tagRepository.findByNames(names);
        final List<Tag> tags = Stream.concat(tagRepository.saveAll(names.stream()
                .filter(name->existingTags.stream().noneMatch(t->t.getName().equals(name)))
                .map(name->new Tag(name))
                .toList()).stream(), existingTags.stream())
                .toList();
        return tags;
    }
}
