package ru.yandex.practicum.controller;


import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.dto.FoundPostsDTO;
import ru.yandex.practicum.model.dto.PostDTO;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.service.PostService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    protected PostController(@NotNull final PostService postService) {
        this.postService = postService;
    }

    /**
     * Найти посты по переданной поисковой строке.
     * @param search поисковая строка.
     * @param pageNumber номер страницы.
     * @param pageSize количество постов на странице.
     * @return ответ с найденными постами в установленной форме.
     */
    @GetMapping()
    @ResponseBody
    protected FoundPostsDTO searchPosts(@RequestParam("search") final String search,
                                        @RequestParam("pageNumber") final int pageNumber,
                                        @RequestParam("pageSize")  final int pageSize) {
        if(Objects.isNull(search) || pageNumber < 1 || pageSize < 1) {
            logger.warn("Неккоректные данные запроса. Ожидается, что номер страницы (pageNumber) не меньше 1, переданный номер: {}. Ожидается, что размер страницы (pageSize) не меньше 1. Переданный размер: {}.", pageNumber, pageSize);
            return FoundPostsDTO.getEmpty();
        }
        final long countPages = postService.getCountPagesForSearchByTitle(search, pageSize);
        if(pageNumber > countPages) {
            logger.warn("Запрашиваемый номер страницы ({}) постов больше количества страниц ({}).",  pageNumber, countPages);
            return FoundPostsDTO.getEmpty(countPages);
        }
        final boolean hasPreview = pageNumber > 1;
        final boolean hasNext = pageNumber < countPages;
        final List<PostDTO> foundRecords = postService.searchAllByTitle(search, pageNumber, pageSize);
        return new FoundPostsDTO(foundRecords, hasPreview, hasNext, countPages);
    }

    /**
     * Найти пост по уникальному номеру.
     * @param id уникальные номер поста (больше 0)
     * @return найденный пост.
     */
    @GetMapping("/{id}")
    @ResponseBody
    protected PostDTO findPostById(@PathVariable("id") final Long id) {
        if(Objects.isNull(id) || id < 1) {
            logger.warn("Некорректный запрос. Уникальный номер поста не может быть ниже 1. Переданный уникальный номер: {}.", id);
            return PostDTO.getEmpty();
        }
        return postService.findById(id).orElse(PostDTO.getEmpty());
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    protected PostDTO savePost(@RequestBody final AddingPostDTO post) {
        return postService.savePost(post);

    }
}
















