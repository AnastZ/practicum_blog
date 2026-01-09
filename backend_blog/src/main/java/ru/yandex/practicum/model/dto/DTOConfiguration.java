package ru.yandex.practicum.model.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.model.DTOMapper;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Configuration
public class DTOConfiguration {

    @Bean
    public DTOMapper<Post, PostDTO> getPostDTOMapper() {
        return new DTOMapper<Post, PostDTO>() {

            @Override
            public Optional<Post> toEntity(@NotNull final PostDTO postDTO) {
                final List<Tag> tags = postDTO.tags().stream()
                        .map(Tag::new)
                        .toList();
                return Optional.of(new Post(postDTO.id(), postDTO.title(), postDTO.text(), postDTO.likesCount(), postDTO.commentsCount(), tags));
            }

            @Override
            public Optional<PostDTO> toDTO(@NotNull final Post entity) {
                final List<String> tags = entity.getTags().stream()
                        .filter(Objects::nonNull)
                        .map(Tag::getName)
                        .toList();
                return Optional.of(new PostDTO(entity.getId(), entity.getTitle(), entity.getText(), tags, entity.getLikesCount(), entity.getCommentsCount()));
            }
        };

    }

}
