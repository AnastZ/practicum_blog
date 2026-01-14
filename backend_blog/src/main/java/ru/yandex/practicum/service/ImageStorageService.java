package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.util.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ImageStorageService {
    /**
     * Допустимые расширения для файлов изображения.
     */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");

    private final FileUtils fileUtils;

    // Путь к директории, где хранятся все изображения постов.
    private final String UPLOAD_DIR;
    private final ResourceLoader resourceLoader;

    protected ImageStorageService(@NotNull final FileUtils fileUtils,
                                  @Value("$post.image.path") @NotNull final String imagePath,
                                  @NotNull final ResourceLoader loader) throws SecurityException{
        this.fileUtils = fileUtils;
        UPLOAD_DIR = imagePath;
        fileUtils.createDirectoryIfNotExists(UPLOAD_DIR);
        this.resourceLoader = loader;
    }


    /**
     * Сохранение изображения в файловой системе.
     *
     * @return новый путь к изображению.
     */
    public String saveOrUpdateImageInStorage(final MultipartFile file,
                                             final String oldImagePath) throws IllegalStateException, SecurityException {
        if(Objects.isNull(file) || file.isEmpty()) {
            throw new IllegalArgumentException("Переданный файл изображения пуст.");
        }
        // Удаление старого файла, если он существует
        if (Objects.nonNull(oldImagePath) && !oldImagePath.isEmpty()) {
            fileUtils.deleteFileIfExists(oldImagePath);
        }
        final String originalFilename = file.getOriginalFilename();
        if (Objects.isNull(originalFilename)) {
            throw new IllegalArgumentException("Наименование файла изображения пустое.");
        }
        final String fileExtension = fileUtils.getFileExtension(originalFilename);

        if (! fileUtils.isValidExtension(fileExtension, ALLOWED_EXTENSIONS)) {
            throw new IllegalArgumentException("Неверное расширение файла изображения.");
        }
        final Path filePath = Paths.get(UPLOAD_DIR + "/" + file.getOriginalFilename());
        try{
            Files.write(filePath, file.getBytes());
        }catch (IOException | UnsupportedOperationException e){
            throw new SecurityException("Не удалось сохранить файл изображения.", e);
        }
        return filePath.toAbsolutePath().toString();
    }

    /**
     * Получить расширение файла.
     *
     * @param imagePath путь к файлу.
     * @return расширение файла.
     * @throws IOException
     */
    public @NotNull MediaType getContentType(@NotNull final String imagePath) throws IOException {
        return fileUtils.getMediaType(imagePath);
    }

    /**
     * Загрузить изображение из файлового хранилища.
     * @param location путь к файлу.
     * @return ресурс файла.
     * @throws IOException
     */
    public @NotNull Resource loadImage(@NotNull final String location) throws NoSuchFileException {
        final Resource resource = resourceLoader.getResource(location);
        if (! resource.exists()) {
            throw new NoSuchFileException("File not found at: " + location);
        }
        return resource;
    }

}
