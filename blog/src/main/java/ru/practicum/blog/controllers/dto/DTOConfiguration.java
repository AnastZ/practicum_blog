package ru.practicum.blog.controllers.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.DTOToEntityMapper;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class DTOConfiguration {

    @Bean
    public DTOMapper<Post, PostDTO> getPostDTOMapper() {
        return new DTOMapper<Post, PostDTO>() {

            @Override
            public Post toEntity(@NotNull final PostDTO postDTO) {
                final List<Tag> tags = postDTO.tags().stream()
                        .map(Tag::new)
                        .toList();
                final Post post = new Post(postDTO.title(), postDTO.text(), tags);
                post.setId(postDTO.id());
                return post;
            }

            @Override
            public PostDTO toDTO(@NotNull final Post entity) {
                final List<String> tags = entity.getTags().stream()
                        .filter(Objects::nonNull)
                        .map(Tag::getName)
                        .toList();
                return new PostDTO(entity.getId(), entity.getTitle(), entity.getText(), tags, entity.getLikesCount(), entity.getCommentsCount());
            }
        };

    }

    @Bean
    public DTOToEntityMapper<Post, InputPostDTO> getUpdatingPostDTOMapper() {
        return updatingPostDTO -> {
            final Post post = new Post(updatingPostDTO.getTitle(),
                    updatingPostDTO.getText(),
                    Collections.emptyList());
            if (!updatingPostDTO.getId().equals(0L)) {
                post.setId(updatingPostDTO.getId());
            }
            return post;
        };
    }
    @Bean
    public DTOMapper<Comment, CommentDTO> getCommentDTOMapper() {
        return new DTOMapper<Comment, CommentDTO>() {
            @Override
            public CommentDTO toDTO(@NotNull final Comment comment) {
                final Long id = comment.getId();
                return new CommentDTO(Objects.isNull(id) ? 0L : id, comment.getText(), comment.getPost().getId());
            }

            @Override
            public Comment toEntity(@NotNull final CommentDTO commentDTO) {
                final Comment c = new Comment(commentDTO.text());
                if(! commentDTO.id().equals(0L)){
                    c.setId(commentDTO.id());
                }
                return c;
            }
        };
    }
}









