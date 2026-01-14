package ru.yandex.practicum.test.unit.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.WebConfig;
import ru.yandex.practicum.test.integration.IntegrationConfig;
import ru.yandex.practicum.util.FileUtils;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class FileUtilsTest {

    @ParameterizedTest
    @CsvSource({"\\src.\\test\\java\\ru\\yandex\\practicum.\\test\\unit\\tst.jpg, .jpg"
    })
    void testGetFileExtension(final String path, final String extension) {
        final FileUtils fileUtils = new FileUtils();
        assertTrue(fileUtils.getFileExtension(path).equals(extension));
    }


}
