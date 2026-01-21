package ru.yandex.practicum.integration;

import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;


@SpringJUnitConfig(classes = {
        IntegrationConfig.class
})
@WebAppConfiguration
public abstract class AbstractIntegrationTest {
}
