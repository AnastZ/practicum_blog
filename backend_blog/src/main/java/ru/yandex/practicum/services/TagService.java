package ru.yandex.practicum.services;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.models.Tag;
import ru.yandex.practicum.repositories.TagRepository;

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

    /**
     * Сохранить из переданного списка новые тэги в БД, загрузить из БД уже существующие, вернув все тэги в виде объектов.
     *
     * @param names наименования тэгов.
     * @return список тэгов с уникальными номерами в БД.
     * @throws Exception
     */
    @Transactional
    public @NotNull List<Tag> saveTagsAndGet(@NotNull final List<String> names) throws Exception {
        final List<String> cleanNames = getClearNames(names);
        if (cleanNames.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Tag> existingTagsFromDB = tagRepository.findByNames(cleanNames);
        final List<String> existingNames = existingTagsFromDB.stream()
                .map(Tag::getName)
                .toList();
        final List<Tag> newTagsToSave = cleanNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(Tag::new)
                .toList();
        if (newTagsToSave.isEmpty()) {
            return existingTagsFromDB;
        }
        try {
            final List<Tag> savedNewTags = tagRepository.saveAll(newTagsToSave);
            return Stream.concat(existingTagsFromDB.stream(), savedNewTags.stream()).toList();
        } catch (OptimisticLockingFailureException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    /**
     * Отфильтровать пустые значения в списоке наименований тегов,
     * преобразовать названия в нижний регистр, убрать повторения.
     *
     * @param names наименования тегов.
     * @return нормализированный список наименований тегов.
     */
    public List<String> getClearNames(@NotNull final List<String> names) {
        return names.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toLowerCase())
                .distinct()
                .toList();
    }
}
