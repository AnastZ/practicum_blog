package ru.yandex.practicum.integration.controller;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class UtilConfiguration {

    @Bean
    @Primary
    public ResourceLoader getResourceLoader() {
        return Mockito.mock(ResourceLoader.class);
    }
    @Bean
    public Resource getImageResource() {
        return Mockito.mock(Resource.class);
    }

}
