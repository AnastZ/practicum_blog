package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.DTOToEntityMapper;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.dto.InputPostDTO;
import ru.yandex.practicum.model.dto.UpdatingPostDTO;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.model.util.Merger;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.Utils;
import ru.yandex.practicum.model.dto.PostDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostService {

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final DTOMapper<Post, PostDTO> dtoMapper;
    private final DTOToEntityMapper<Post, InputPostDTO> inputPostDTOMapper;
    private final Merger<Post> postMerger;
    private final TagService tagService;

    public PostService(@NotNull final PostRepository postRepository,
                       @NotNull final DTOMapper<Post, PostDTO> dtoMapper,
                       @NotNull final TagService tagService,
                       @NotNull final DTOToEntityMapper<Post, InputPostDTO> inputPostDTOMapper,
                       @NotNull final Merger<Post> postMerger) {
        this.postRepository = postRepository;
        this.dtoMapper = dtoMapper;
        this.tagService = tagService;
        this.inputPostDTOMapper = inputPostDTOMapper;
        this.postMerger = postMerger;
    }


    /**
     * Получить количество страниц с постами относительно переданного количества постов на одной странице.
     *
     * @param pageSize количество постов на одной странице.
     * @return количество постов относительно переданного количества на одной странице.
     */
    public long getCountPagesForSearchByTitle(@NotNull final String searchString,
                                              final int pageSize) {

        final long countRecords = postRepository.getCountRecordInSearchQuery(searchString);
        return Utils.calculateCountPages(countRecords, pageSize);
    }

    /**
     * Найти посты в БД по поисковому запросу на заданной странице.
     * Обрезать текст поста до 128 символов.
     *
     * @param searchString поисковой запрос.
     * @param pageNumber   номер страницы.
     * @param pageSize     количество записей на странице.
     * @return результаты поиска в БД по поисковому запросу.
     */
    public List<PostDTO> searchAllByTitle(@NotNull final String searchString,
                                          final int pageNumber,
                                          final int pageSize) throws Exception {

        final List<Post> posts = postRepository.findAllByStringQuery(searchString, pageNumber - 1, pageSize).stream().toList();
        return posts.stream()
                .filter(Objects::nonNull)
                .peek(p -> {
                    final String text = p.getText();
                    if (text.length() <= 128) {
                        return;
                    }
                    p.setText(text.substring(0, 128) + "...");
                })
                .map(dtoMapper::toDTO)
                .toList();
    }

    /**
     * Найти пост по уникальному номеру в БД.
     *
     * @param id уникальный номер поста.
     * @return результат поиска.
     */
    @Transactional
    public Post findById(@NotNull final Long id) throws Exception {
        return postRepository.findById(id);

    }

    /**
     * Найти пост по уникальному номеру в БД и преобразовать его в PostDTO.
     *
     * @param id уникальный номер поста.
     * @return
     */
    public PostDTO findByIdAndGetDTO(@NotNull final Long id) throws Exception {
        return dtoMapper.toDTO(findById(id));
    }

    /**
     * Сохранить новый пост.
     *
     * @param addingPost данные нового поста.
     * @return новый пост с уникальным номером из БД.
     */
    @Transactional
    public PostDTO savePost(@NotNull final InputPostDTO addingPost) throws Exception {
        final Post newPost = inputPostDTOMapper.toEntity(addingPost);
        final List<Tag> tags = tagService.findByNames(addingPost.getTags());
        newPost.setTags(tags);

        final Post post = postRepository.save(newPost);
        return dtoMapper.toDTO(post);

    }

    /**
     * Сначала из БД загружается прежний объект (по id), затем производится слияние новых данных и прежних (из БД берётся: число лайков и дата создания)
     *
     * @param updatingPost новые данные поста.
     * @return обновлённый пост.
     */
    @Transactional
    public PostDTO updatePost(@NotNull final InputPostDTO updatingPost) throws Exception {
        final Post postById = this.findById(updatingPost.getId());
        final Post updatedPost = inputPostDTOMapper.toEntity(updatingPost);
        final List<Tag> tags = tagService.findByNames(updatingPost.getTags());
        updatedPost.setTags(tags);
        postMerger.merge(postById, updatedPost);

        final Post post = postRepository.save(updatedPost);
        return dtoMapper.toDTO(post);

    }
    @Transactional
    public void deletePost(@NotNull final Long id) throws Exception {
        postRepository.delete(id);
    }
}
