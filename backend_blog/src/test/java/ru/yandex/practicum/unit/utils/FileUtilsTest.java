package ru.yandex.practicum.unit.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import unit.AbstractUnitTest;
import ru.yandex.practicum.util.FileUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileUtilsTest extends AbstractUnitTest {

    @ParameterizedTest
    @CsvSource({"\\src.\\test\\java\\ru\\yandex\\practicum.\\test\\unit\\tst.jpg, .jpg"
    })
    void testGetFileExtension(final String path, final String extension) {
        final FileUtils fileUtils = new FileUtils();
        assertTrue(fileUtils.getFileExtension(path).equals(extension));
    }


}
