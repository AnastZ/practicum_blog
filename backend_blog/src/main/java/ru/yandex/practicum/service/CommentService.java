package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.dto.CommentDTO;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    /**
     * Получить все комментарии поста по его id.
     * @param postId уникальный номер поста.
     * @return список комментариев поста.
     * @throws Exception
     */
    @Transactional
    public List<CommentDTO> getComments(final Long postId) throws Exception {
        return commentRepository.getCommentsByPostId(postId)
                .stream()
                .filter(Objects::nonNull)
                .map(c->dtoMapper.toDTO(c))
                .toList();
    }

    /**
     * Получить комментарий по его id и id поста.
     * @param postId уникальный номер поста.
     * @param commentId уникальный номер комментария.
     * @return найденный комментарий.
     * @throws Exception
     */
    @Transactional
    public CommentDTO getCommentsByPostId(final Long postId,
                                          final Long commentId) throws Exception {
        return dtoMapper.toDTO(commentRepository.getCommentByIdAndPostId(commentId, postId));
    }

    /**
     * Сохранить комментарий.
     * @param commentDTO комментарий.
     * @return сохранённый комментарий.
     * @throws Exception
     */
    @Transactional
    public CommentDTO save(@NotNull final CommentDTO commentDTO) throws Exception {
        final Comment dtoComment = dtoMapper.toEntity(commentDTO);
        if(! commentDTO.id().equals(0L)){
            final Comment existingComment = commentRepository.getCommentById(dtoComment.getId());
            if(Objects.isNull(existingComment) ||
                    ! existingComment.getPost().getId().equals(commentDTO.postId())) {
                throw new IllegalArgumentException("Переданного комментария не существует, либо он принадлежит другому посту.");
            }
        }
        final Post post = postService.findById(commentDTO.postId());
        dtoComment.setPost(post);
        final Comment newComment = commentRepository.save(dtoComment);
        return dtoMapper.toDTO(newComment);
    }

    /**
     * Удалить комментарий из БД.
     * @param idPost
     * @param idComment уникальный номер комментария.
     * @throws Exception
     */
    @Transactional
    public void delete(@NotNull final Long idPost,
                       @NotNull final Long idComment) throws Exception {
        commentRepository.delete(idPost, idComment);
    }
}
