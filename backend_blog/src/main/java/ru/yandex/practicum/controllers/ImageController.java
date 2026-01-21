package ru.yandex.practicum.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.ImageService;

@RestController
@RequestMapping("/api/posts/{id}/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(@NotNull final ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Обновить изображение поста.
     *
     * @param postId уникальный номер поста.
     * @param image  объект изображения.
     * @throws Exception
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    protected void updatePostImage(@PathVariable("id") final Long postId,
                                   @RequestParam("image") final MultipartFile image) throws Exception {

        imageService.updateImage(postId, image);
    }

    /**
     * Получить байты изображения поста.
     *
     * @param id уникальный номер поста.
     * @return
     * @throws Exception
     */
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    protected ResponseEntity<byte[]> getPostImage(@PathVariable("id") final Long id) throws Exception {
        return imageService.getImageAsByte(id);
    }

}
