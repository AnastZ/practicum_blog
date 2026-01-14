package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.dto.CommentDTO;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final DTOMapper<Comment, CommentDTO> dtoMapper;
    private final PostService postService;

    public CommentService(@NotNull final CommentRepository commentRepository,
                          @NotNull final DTOMapper<Comment, CommentDTO> dtoMapper,
                          @NotNull final PostService postService) {
        this.commentRepository = commentRepository;
        this.dtoMapper = dtoMapper;
        this.postService = postService;
    }

    @Transactional
    public List<CommentDTO> getComments(final Long postId) throws Exception {
        return commentRepository.getCommentsByPostId(postId)
                .stream()
                .map(c->dtoMapper.toDTO(c))
                .toList();
    }
    @Transactional
    public CommentDTO getCommentsByPostId(final Long postId,
                                          final Long commentId) throws Exception {
        return dtoMapper.toDTO(commentRepository.getCommentByIdAndPostId(commentId, postId));
    }
    @Transactional
    public CommentDTO save(@NotNull final CommentDTO commentDTO) throws Exception {
        final Comment comment = dtoMapper.toEntity(commentDTO);
        comment.setPost(postService.findById(commentDTO.postId()));
        return dtoMapper.toDTO(commentRepository.save(comment));
    }
    @Transactional
    public void delete(@NotNull final Long id) throws Exception {
        commentRepository.delete(id);
    }
}
