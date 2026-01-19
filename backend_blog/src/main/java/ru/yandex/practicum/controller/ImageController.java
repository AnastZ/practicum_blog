package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.service.ImageService;
import ru.yandex.practicum.util.EntityValidator;

import java.util.Objects;

@RestController
@RequestMapping("/api/posts/{id}/image")
public class ImageController {

    private final ImageService imageService;
    private final EntityValidator<Long> postIdValidator;

    public ImageController(@NotNull final ImageService imageService,
                           @NotNull final EntityValidator<Long> postIdValidator) {
        this.imageService = imageService;
        this.postIdValidator = postIdValidator;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    protected void updatePostImage(@PathVariable("id") final Long postId,
                                   @RequestParam("image") final MultipartFile image) throws Exception {
        postIdValidator.validate(postId);
        if(Objects.isNull(image)) {
            throw new IllegalArgumentException("image is null");
        }
        imageService.updateImage(postId, image);
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    protected ResponseEntity<byte[]> getPostImage(@PathVariable("id") final Long id) throws Exception {
        postIdValidator.validate(id);
        return imageService.getImageAsByte(id);
    }
}
