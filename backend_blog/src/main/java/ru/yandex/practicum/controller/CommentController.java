package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.dto.CommentDTO;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.util.EntityValidator;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final EntityValidator<Long> idValidator;

    public CommentController(@NotNull final CommentService commentService,
                             @NotNull final EntityValidator<Long> idValidator) {
        this.commentService = commentService;
        this.idValidator = idValidator;
    }


    @GetMapping
    @ResponseBody
    protected List<CommentDTO> getPostComments(@PathVariable("postId") final Long postId) throws Exception {
        idValidator.validate(postId);
        return commentService.getComments(postId);
    }

    @GetMapping("/{commentId}")
    @ResponseBody
    protected CommentDTO getPostComment(@PathVariable("postId") final Long postId,
                                        @PathVariable("commentId") final Long commentId) throws Exception {
        idValidator.validate(postId);
        return commentService.getCommentsByPostId(postId, commentId);
    }

    @PostMapping
    @ResponseBody
    protected CommentDTO save(@PathVariable("postId") final Long postId,
                              @RequestBody final CommentDTO dto) throws Exception {
        idValidator.validate(postId);
        return commentService.save(dto);
    }

    @PutMapping("/{commentId}")
    @ResponseBody
    protected CommentDTO update(@PathVariable("postId") final Long postId,
                                @PathVariable("commentId") final Long commentId,
                                @RequestBody final CommentDTO dto) throws Exception {
        idValidator.validate(postId);
        return commentService.save(dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    protected void delete(@PathVariable("postId") final Long postId,
                          @PathVariable("commentId") final Long commentId) throws Exception {
        idValidator.validate(postId);
        commentService.delete(postId);
    }
}
