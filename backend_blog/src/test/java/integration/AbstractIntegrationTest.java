package integration;

import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.WebConfig;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
public abstract class AbstractIntegrationTest {
}
