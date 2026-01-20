package ru.practicum.blog.services;

import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.blog.controllers.DTOMapper;
import ru.practicum.blog.controllers.dto.CommentDTO;
import ru.practicum.blog.models.Comment;
import ru.practicum.blog.models.Post;
import ru.practicum.blog.repositories.CommentRepository;
import ru.practicum.blog.util.EntityValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final DTOMapper<Comment, CommentDTO> dtoMapper;
    private final PostService postService;
    private final EntityValidator<Long> idValidator;

    public CommentService(@NotNull final CommentRepository commentRepository,
                          @NotNull final DTOMapper<Comment, CommentDTO> dtoMapper,
                          @NotNull final PostService postService,
                          @NotNull final EntityValidator<Long> idValidator) {
        this.commentRepository = commentRepository;
        this.dtoMapper = dtoMapper;
        this.postService = postService;
        this.idValidator = idValidator;
    }

    /**
     * Получить все комментарии поста по его id.
     * @param postId уникальный номер поста.
     * @return список комментариев поста.
     * @throws Exception
     */
    @Transactional
    public List<CommentDTO> getComments(final Long postId) throws IllegalArgumentException {
        idValidator.validate(postId);
        return commentRepository.findByPostId(postId)
                .stream()
                .filter(Objects::nonNull)
                .map(dtoMapper::toDTO)
                .toList();
    }

    /**
     * Получить комментарий по его id и id поста.
     * @param postId уникальный номер поста.
     * @param commentId уникальный номер комментария.
     * @return найденный комментарий.
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public CommentDTO getCommentsByPostId(final Long postId,
                                          final Long commentId) throws NoResultException, IllegalArgumentException {
        idValidator.validate(postId);
        idValidator.validate(commentId);
        final Optional<Comment> comment = commentRepository.findByIdAndPost(commentId, postId);
        if(comment.isEmpty()){
            throw new NoResultException("Comment not found.");
        }
        return dtoMapper.toDTO(comment.get());
    }

    /**
     * Сохранить комментарий.
     * @param commentDTO комментарий.
     * @return сохранённый комментарий.
     * @throws Exception
     */
    @Transactional
    public CommentDTO save(@NotNull final CommentDTO commentDTO) throws NoResultException, IllegalArgumentException, DataIntegrityViolationException {
        idValidator.validate(commentDTO.postId());
        final Comment dtoComment = dtoMapper.toEntity(commentDTO);
        if(! commentDTO.id().equals(0L)){
            final Optional<Comment> existingComment = commentRepository.findById(dtoComment.getId());
            if(existingComment.isEmpty() ||
                    ! existingComment.get().getPost().getId().equals(commentDTO.postId())) {
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
     * @param idPost уникальный номер поста.
     * @param idComment уникальный номер комментария.
     * @throws Exception
     */
    @Transactional
    public void delete(@NotNull final Long idPost,
                       @NotNull final Long idComment) throws IllegalArgumentException, EmptyResultDataAccessException {
        idValidator.validate(idPost);
        idValidator.validate(idComment);
        final Optional<Comment> comment = commentRepository.findByIdAndPost(idComment, idPost);
        if(comment.isEmpty()){
            throw new IllegalArgumentException("Comment not found.");
        }
        commentRepository.delete(comment.get());
    }
}
