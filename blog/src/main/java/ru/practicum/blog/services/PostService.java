package ru.practicum.blog.services;

import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.blog.controllers.DTOMapper;
import ru.practicum.blog.controllers.DTOToEntityMapper;
import ru.practicum.blog.controllers.dto.InputPostDTO;
import ru.practicum.blog.controllers.dto.PostDTO;
import ru.practicum.blog.models.Post;
import ru.practicum.blog.repositories.PostRepository;
import ru.practicum.blog.services.util.Merger;

import java.util.*;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public @NotNull Post findById(@NotNull final Long id) throws NoResultException {
        final Post p = postRepository.findById(id);
        if (Objects.isNull(p)) {
            throw new NoResultException("Post with id " + id + " does not exist");
        }
        return p;
    }

    /**
     * Найти пост по уникальному номеру в БД и преобразовать его в PostDTO.
     *
     * @param id уникальный номер поста.
     * @return
     */
    @Transactional(readOnly = true)
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
        final List<Tag> tags = tagService.saveTagsAndGet(addingPost.getTags());
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
        final List<Tag> tags = tagService.saveTagsAndGet(updatingPost.getTags());
        updatedPost.setTags(tags);
        postMerger.merge(postById, updatedPost);

        final Post post = postRepository.save(updatedPost);
        return dtoMapper.toDTO(post);

    }

    /**
     * Удаление поста по уникальному номеру.
     * @param id уникальный номер поста.
     * @throws Exception
     */
    @Transactional
    public void deletePost(@NotNull final Long id) throws Exception {
        postRepository.delete(id);
    }

    /**
     * Инкремент количества лайков для поста по уникальному номеру.
     * @param id уникальный номер поста.
     * @return инкрементированное количество лайков.
     * @throws Exception
     */
    @Transactional
    public Long incrementLikes(@NotNull final Long id) throws Exception {
        return postRepository.incrementLikes(id);
    }
    @Transactional
    public void updatePostImagePath(@NotNull final Long postId,
                                    @NotNull final String imagePath) throws Exception {
        postRepository.updatePostImagePath(postId, imagePath);
    }

}
