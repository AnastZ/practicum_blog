package ru.practicum.blog.services.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.service.PostService;

@Configuration
public class MergerConfig {

    @Bean
    public Merger<Post> getPostMerger() {
        return (template, updatedPost) -> {
            updatedPost.setCreatedDate(template.getCreatedDate());
            updatedPost.setLikesCount(template.getLikesCount());
            updatedPost.setCommentsCount(template.getCommentsCount());
            return updatedPost;
        };
    }
}
