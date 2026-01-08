package ru.yandex.practicum.controller;


import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.FoundPostsDTO;
import ru.yandex.practicum.model.dto.PostDTO;
import ru.yandex.practicum.service.PostService;

import java.util.List;

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
    protected FoundPostsDTO searchPosts(@RequestParam("search") @NotNull final String search,
                                        @RequestParam("pageNumber") final int pageNumber,
                                        @RequestParam("pageSize")  final int pageSize) {

        boolean hasPreview = pageNumber > 1;
        final long countPages = postService.getCountPagesForSearchByTitle(search, pageSize);
        boolean hasNext = pageNumber < countPages;
        final List<PostDTO> foundRecords = postService.searchAllByTitle(search, pageNumber, pageSize);
        return new FoundPostsDTO(foundRecords, hasPreview, hasNext, countPages);
    }


}
















