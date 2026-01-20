package ru.practicum.blog.services;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.blog.models.Tag;
import ru.practicum.blog.repositories.TagRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class TagService {

    private final Logger logger = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(@NotNull final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public @NotNull List<Tag> saveTagsAndGet(@NotNull final List<String> names) throws Exception{
        final List<String> cleanNames = names.stream()
                .filter(Objects::nonNull)
                .map(s->s.trim().toLowerCase())
                .distinct()
                .toList();
        if (cleanNames.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Tag> existingTagsFromDB = tagRepository.findByNames(cleanNames);
        final List<String> existingNames = existingTagsFromDB.stream()
                .map(Tag::getName)
                .toList();
        final List<Tag> newTagsToSave = cleanNames.stream()
                .filter(name -> ! existingNames.contains(name))
                .map(Tag::new)
                .toList();
        if(newTagsToSave.isEmpty()){
            return existingTagsFromDB;
        }
        final List<Tag> savedNewTags = tagRepository.saveAll(newTagsToSave);
        return Stream.concat(existingTagsFromDB.stream(), savedNewTags.stream()).toList();
    }

}
