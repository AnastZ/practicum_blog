package ru.yandex.practicum.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.yandex.practicum.services.ImageService;
import ru.yandex.practicum.services.ImageStorageService;
import ru.yandex.practicum.services.PostService;

@TestConfiguration
public class ServiceConfig {

    @Bean
    public ImageService imageService(PostService postService, ImageStorageService imageStorageService) {
        return new ImageService(postService, imageStorageService);
    }
}
