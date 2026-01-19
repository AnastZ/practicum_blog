package ru.yandex.practicum.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
@Configuration
public class ValidatorConfiguration {
    @Bean
    public EntityValidator<Long> getIdValidator() {
        return new  EntityValidator<Long>() {
            @Override
            public void validate(Long id) throws IllegalArgumentException {
                if(Objects.isNull(id) || id < 1) {
                    throw new IllegalArgumentException("Некорректный запрос. Уникальный номер поста не может быть ниже 1.");
                }
            }
        };
    }
}
