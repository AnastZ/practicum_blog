package ru.practicum.blog.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.blog.controllers.dto.CommentDTO;
import ru.practicum.blog.services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(@NotNull final CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получить список комментариев, относящихся к посту.
     * @param postId уникальный номер поста.
     * @return список найденных комментариев.
     * @throws Exception
     */
    @GetMapping
    @ResponseBody
    protected List<CommentDTO> getPostComments(@PathVariable("postId") final Long postId) throws Exception {
        return commentService.getComments(postId);
    }

    /**
     * Получить комментарий по id и id поста.
     * @param postId уникальный номер поста.
     * @param commentId уникальный номер комментария.
     * @return найденный комментарий.
     * @throws Exception
     */
    @GetMapping("/{commentId}")
    @ResponseBody
    protected CommentDTO getPostComment(@PathVariable("postId") final Long postId,
                                        @PathVariable("commentId") final Long commentId) throws Exception {
        return commentService.getCommentsByPostId(postId, commentId);
    }

    /**
     * Сохранить новый комментарий.
     * @param postId уникальный номер поста.
     * @param dto комментарий.
     * @return сохранённый комментарий.
     * @throws Exception
     */
    @PostMapping
    @ResponseBody
    protected CommentDTO save(@PathVariable("postId") final Long postId,
                              @RequestBody final CommentDTO dto) throws Exception {
        return commentService.save(dto);
    }

    /**
     * Изменить существующий комментарий.
     * @param postId уникальный номер поста.
     * @param commentId униклаьный номер комментария.
     * @param dto объект с новыми данными комментария.
     * @return сохранённый комментарий.
     * @throws Exception
     */
    @PutMapping("/{commentId}")
    @ResponseBody
    protected CommentDTO update(@PathVariable("postId") final Long postId,
                                @PathVariable("commentId") final Long commentId,
                                @RequestBody final CommentDTO dto) throws Exception {
        if(! commentId.equals(dto.id())){
            throw new IllegalArgumentException("CommentId and postId do not match.");
        }
        return commentService.save(dto);
    }

    /**
     * Удалить комментарий.
     * @param postId уникальный номер поста.
     * @param commentId уникальный номер комментария.
     * @throws Exception
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    protected void delete(@PathVariable("postId") final Long postId,
                          @PathVariable("commentId") final Long commentId) throws Exception {
        commentService.delete(postId, commentId);
    }
}
