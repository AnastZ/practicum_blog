package ru.yandex.practicum.services;

import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controllers.DTOMapper;
import ru.yandex.practicum.controllers.DTOToEntityMapper;
import ru.yandex.practicum.controllers.dto.InputPostDTO;
import ru.yandex.practicum.controllers.dto.PostDTO;
import ru.yandex.practicum.models.Post;
import ru.yandex.practicum.models.Tag;
import ru.yandex.practicum.repositories.PostRepository;
import ru.yandex.practicum.services.util.Merger;
import ru.yandex.practicum.util.EntityValidator;

import java.util.*;

@Service
public class PostService {

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final DTOMapper<Post, PostDTO> dtoMapper;
    private final DTOToEntityMapper<Post, InputPostDTO> inputPostDTOMapper;
    private final Merger<Post> postMerger;
    private final TagService tagService;
    private final EntityValidator<Long> idValidator;
    private final int titleShortLength;

    public PostService(@NotNull final PostRepository postRepository,
                       @NotNull final DTOMapper<Post, PostDTO> dtoMapper,
                       @NotNull final TagService tagService,
                       @NotNull final DTOToEntityMapper<Post, InputPostDTO> inputPostDTOMapper,
                       @NotNull final Merger<Post> postMerger,
                       @NotNull final EntityValidator<Long> idValidator,
                       @Value("${post.title.short-length}") final int titleShortLength) {
        this.postRepository = postRepository;
        this.dtoMapper = dtoMapper;
        this.tagService = tagService;
        this.inputPostDTOMapper = inputPostDTOMapper;
        this.postMerger = postMerger;
        this.idValidator = idValidator;
        this.titleShortLength = titleShortLength;
    }

    /**
     * Операция сохранения поста в БД. В методе обрабатывается ситуация возникновения исключения OptimisticLockingFailureException, оно оборачивается в IllegalArgumentException.
     *
     * @param post сохраняемый пост.
     * @return сохранённый пост.
     * @throws IllegalArgumentException
     */
    private Post savePost(@NotNull final Post post) throws IllegalArgumentException {
        try {
            return postRepository.save(post);
        } catch (OptimisticLockingFailureException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Распаковка поста, если в обёртке пусто, то вызывается исключение.
     *
     * @param post обёртка с потом.
     * @return распакованный пост.
     * @throws NoResultException если обёртка пуста.
     */
    private @NotNull Post ifNotPresentThrows(final Optional<Post> post) throws NoResultException {
        if (post.isEmpty()) {
            throw new NoResultException("Пост не найден в БД.");
        }
        return post.get();
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

        return postRepository.findIdsByTitle(searchString, Pageable.ofSize(pageSize)).getTotalPages();
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
        final List<Long> ids = postRepository.findIdsByTitle(searchString, Pageable.ofSize(pageSize).withPage(pageNumber - 1)).getContent();
        final List<Post> posts = postRepository.findAllByIds(ids);

        return posts.stream()
                .filter(Objects::nonNull)
                .filter(p -> Objects.nonNull(p.getId()) && p.getId() > 1L)
                .filter(p -> p.getTitle().contains(searchString))
                .filter(p -> p.getTitle().isEmpty())
                .peek(p -> {
                    final String text = p.getText();
                    if (text.length() <= titleShortLength) {
                        return;
                    }
                    p.setText(text.substring(0, titleShortLength) + "...");
                })
                .map(dtoMapper::toDTO)
                .toList();
    }

    /**
     * Найти пост по уникальному номеру в БД.
     *
     * @param id уникальный номер поста.
     * @return результат поиска.
     * @throws NoResultException        если объект не найден в БД.
     * @throws IllegalArgumentException если id поста < 1.
     */
    @Transactional(readOnly = true)
    public @NotNull Post findById(@NotNull final Long id) throws NoResultException, IllegalArgumentException {
        idValidator.validate(id);
        final Optional<Post> p = postRepository.findById(id);
        return ifNotPresentThrows(p);
    }

    /**
     * Найти пост по уникальному номеру в БД и преобразовать его в PostDTO.
     *
     * @param id уникальный номер поста.
     * @return
     * @throws IllegalArgumentException если id поста < 1.
     */
    @Transactional(readOnly = true)
    public PostDTO findByIdAndGetDTO(@NotNull final Long id) throws Exception {
        idValidator.validate(id);
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
        final Post post = this.savePost(newPost);
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
        return dtoMapper.toDTO(this.savePost(updatedPost));
    }

    /**
     * Удаление поста по уникальному номеру.
     *
     * @param id уникальный номер поста.
     * @throws Exception
     * @throws IllegalArgumentException если id поста < 1 или ошибка OptimisticLockingFailureException.
     */
    @Transactional
    public void deletePost(@NotNull final Long id) throws IllegalArgumentException {
        idValidator.validate(id);
        try {
            postRepository.deleteById(id);
        } catch (OptimisticLockingFailureException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    /**
     * Инкремент количества лайков для поста по уникальному номеру.
     *
     * @param postId уникальный номер поста.
     * @return инкрементированное количество лайков.
     * @throws Exception
     * @throws IllegalArgumentException если id поста < 1.
     */
    @Transactional
    public Long incrementLikes(@NotNull final Long postId) throws Exception {
        idValidator.validate(postId);
        final Optional<Post> p = postRepository.findById(postId);
        return this.savePost(ifNotPresentThrows(p).increaseLikesCount()).getLikesCount();
    }

    /**
     *
     * @param postId    уникальный номер поста.
     * @param imagePath новый путь к изображени.
     * @throws NoResultException        если пост не найден в БД.
     * @throws IllegalArgumentException если id поста < 1.
     */
    @Transactional
    public void updatePostImagePath(@NotNull final Long postId,
                                    @NotNull final String imagePath) throws NoResultException, IllegalArgumentException {
        final Post p = this.findById(postId);
        p.setImagePath(imagePath);
        this.savePost(p);
    }
}