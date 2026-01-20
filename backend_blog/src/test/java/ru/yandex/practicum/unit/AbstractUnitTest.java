package ru.yandex.practicum.unit;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.WebConfig;

@SpringJUnitConfig(classes = {
        UnitConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public abstract class AbstractUnitTest {
}
