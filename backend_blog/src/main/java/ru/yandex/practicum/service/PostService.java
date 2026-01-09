package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.Utils;
import ru.yandex.practicum.model.dto.PostDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final DTOMapper<Post, PostDTO> dtoMapper;
    private final TagService tagService;

    public PostService(@NotNull final PostRepository postRepository,
                       @NotNull final DTOMapper<Post, PostDTO> dtoMapper,
                       @NotNull final TagService tagService) {
        this.postRepository = postRepository;
        this.dtoMapper = dtoMapper;
        this.tagService = tagService;
    }


    /**
     * Получить количество страниц с постами относительно переданного количества постов на одной странице.
     * @param pageSize количество постов на одной странице.
     * @return количество постов относительно переданного количества на одной странице.
     */
    public long getCountPagesForSearchByTitle(@NotNull final String searchString,
                                             final int pageSize){
        final long countRecords = postRepository.getCountRecordInSearchQuery(searchString);
        return Utils.calculateCountPages(countRecords, pageSize);
    }

    /**
     * Найти посты в БД по поисковому запросу на заданной странице.
     * Обрезать текст поста до 128 символов.
     * @param searchString поисковой запрос.
     * @param pageNumber номер страницы.
     * @param pageSize количество записей на странице.
     * @return результаты поиска в БД по поисковому запросу.
     */
    public List<PostDTO> searchAllByTitle(@NotNull final String searchString,
                                          final int pageNumber,
                                          final int pageSize) {
        final List<Post> posts = postRepository.findAllByStringQuery(searchString, pageNumber-1, pageSize).stream().toList();
        return posts.stream()
                .filter(Objects::nonNull)
                .peek(p->{
                    final String text = p.getText();
                    if(text.length() <= 128){
                        return;
                    }
                    p.setText(text.substring(0, 128) + "...");
                })
                .map(p->dtoMapper.toDTO(p))
                .filter(pdo->pdo.isPresent())
                .map(Optional::get)
                .toList();
    }

    /**
     * Найти пост по уникальному номеру в БД.
     * @param id уникальный номер поста.
     * @return результат поиска.
     */
    public Optional<PostDTO> findById(@NotNull final Long id) {
        return postRepository.findById(id).flatMap(dtoMapper::toDTO);
    }
    @Transactional
    public PostDTO savePost(@NotNull final AddingPostDTO addingPost) {
        final List<Tag> tags = tagService.findByNames(addingPost.tags());
        final Post newPost = new Post(addingPost.title(),
                addingPost.text(),
                0L,
                0L,
                LocalDate.now(),
                tags);
        final Post post = postRepository.save(newPost);
        return dtoMapper.toDTO(post).orElse(PostDTO.getEmpty());
    }
}
