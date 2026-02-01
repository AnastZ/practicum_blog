package ru.yandex.practicum.unit.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.util.EntityValidator;
import ru.yandex.practicum.util.ValidatorConfiguration;

@TestConfiguration
public class UtilConfig {
    @Bean
    public EntityValidator<Long> getIdValidator() {
        return new ValidatorConfiguration().getIdValidator();
    }
}
