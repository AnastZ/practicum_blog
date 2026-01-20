package ru.practicum.blog.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.blog.models.Post;


import java.nio.file.NoSuchFileException;
import java.util.Objects;

@Service
public class ImageService {

    private final PostService postService;
    private final ImageStorageService  imageStorageService;

    public ImageService(@NotNull final PostService postService,
                        @NotNull final ImageStorageService imageStorageService) {
        this.postService = postService;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Обновить изображение поста. Старое изображение удаляется, в файловой системе сохраняется новое, затем в БД сохраняется путь к новому изображению.
     * @param postId уникальный номер поста.
     * @param image файл изображения.
     * @throws Exception
     */
    @Transactional
    public void updateImage(@NotNull final Long postId, @NotNull final MultipartFile image) throws Exception {
        if(Objects.isNull(image)) {
            throw new IllegalArgumentException("image is null");
        }
        final Post post = postService.findById(postId);
        postService.updatePostImagePath(postId, imageStorageService.saveOrUpdateImageInStorage(image, post.getImagePath()));
    }

    /**
     * Получить изображение поста.
     * @param id уникальный номер поста.
     * @return массив байт обёрнутый в ответ.
     * @throws Exception
     */
    @Transactional
    public ResponseEntity<byte[]> getImageAsByte(@NotNull final Long id) throws Exception {

        final Post post = postService.findById(id);

        final String location = post.getImagePath();

        if(Objects.isNull(location)){
            throw new NoSuchFileException("У поста отсуствует изображение.");
        }
        final Resource resource = imageStorageService.loadImage(location);
        final byte[] body = resource.getContentAsByteArray();
        final MediaType type = imageStorageService.getContentType(location);

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.parseMediaType(type.toString()))
                .body(body);
    }




}
