package ru.yandex.practicum.controller;


import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.dto.FoundPostsDTO;
import ru.yandex.practicum.model.dto.PostDTO;
import ru.yandex.practicum.model.dto.UpdatingPostDTO;
import ru.yandex.practicum.service.PostService;
import ru.yandex.practicum.util.EntityValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    private final EntityValidator<Long> postIdValidator;

    protected PostController(@NotNull final PostService postService,
                             @NotNull final EntityValidator<Long> postIdValidator) {
        this.postService = postService;
        this.postIdValidator = postIdValidator;
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
                                        @RequestParam("pageSize")  final int pageSize) throws Exception {
        if(Objects.isNull(search) || pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Неккоректные данные запроса. Ожидается, что номер страницы (pageNumber) не меньше 1, переданный номер: " + pageNumber +
                    ". Ожидается, что размер страницы (pageSize) не меньше 1. Переданный размер: " + pageSize + ".");
        }
        final long countPages = postService.getCountPagesForSearchByTitle(search, pageSize);
        if(pageNumber > countPages) {
            throw new IllegalArgumentException("Запрашиваемый номер страницы постов больше количества страниц." +
                    "\nПереданный номер страницы:" + pageNumber + ", количество страниц всего:" + countPages);
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
    protected PostDTO findPostById(@PathVariable("id") final Long id) throws Exception {
        postIdValidator.validate(id);
        return postService.findByIdAndGetDTO(id);
    }

    /**
     * Сохранить новый пост и вернуть его же с присвоенным уникальным номером из БД.
     * @param post создаваемый пост.
     * @return пост с присвоенным уникальным номером.
     * @throws Exception
     */
    @PostMapping
    @ResponseBody
    protected PostDTO savePost(@RequestBody final AddingPostDTO post) throws Exception {
        return postService.savePost(post);
    }

    /**
     * Обновить данные поста.
     * @param id уникальный номер обновляемого поста.
     * @param post обновляемый пост.
     * @return обновлённый пост.
     * @throws Exception
     */
    @PutMapping("/{id}")
    @ResponseBody
    protected PostDTO updatePost(@PathVariable("id") final Long id,
                               @RequestBody final UpdatingPostDTO post) throws Exception {
        postIdValidator.validate(id);
        if(! post.getId().equals(id)) {
            throw new IllegalArgumentException("Данные переданные в параметре запроса и тело не совпадают." + post.getId() + " " + id);
        }
        return postService.updatePost(post);
    }

    /**
     * Удаление поста по уникальному номеру.
     * @param id уникальный номер поста.
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected void deletePost(@PathVariable("id") final Long id) throws Exception {
        postIdValidator.validate(id);
        postService.deletePost(id);
    }

    @PostMapping("/{id}/likes")
    @ResponseBody
    protected Long likePost(@PathVariable("id") final Long id) throws Exception {
        postIdValidator.validate(id);
        return postService.incrementLikes(id);
    }



}
















