package integration.controller;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.service.ImageStorageService;
import ru.yandex.practicum.service.PostService;

@Configuration
public class ServiceConfiguration {
    @Bean
    @Primary
    public PostService getPostService() {
        return Mockito.mock(PostService.class);
    }
    @Bean
    @Primary
    public ImageStorageService getImageStorageService() {
        return Mockito.mock(ImageStorageService.class);
    }


}
