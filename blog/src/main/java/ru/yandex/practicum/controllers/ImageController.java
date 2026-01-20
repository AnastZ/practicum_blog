package ru.practicum.blog.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.blog.services.ImageService;

import java.util.Objects;

@RestController
@RequestMapping("/api/posts/{id}/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(@NotNull final ImageService imageService) {
        this.imageService = imageService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    protected void updatePostImage(@PathVariable("id") final Long postId,
                                   @RequestParam("image") final MultipartFile image) throws Exception {

        imageService.updateImage(postId, image);
    }
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    protected ResponseEntity<byte[]> getPostImage(@PathVariable("id") final Long id) throws Exception {
        return imageService.getImageAsByte(id);
    }
}
