package ru.yandex.practicum.integration.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.controllers.ImageController;
import ru.yandex.practicum.integration.AbstractIntegrationTest;
import ru.yandex.practicum.integration.PostIdGenerator;
import ru.yandex.practicum.integration.ServiceConfig;
import ru.yandex.practicum.models.Post;
import ru.yandex.practicum.services.ImageService;
import ru.yandex.practicum.services.ImageStorageService;
import ru.yandex.practicum.services.PostService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ImageController.class)
@AutoConfigureMockMvc
@Import(ServiceConfig.class)
public class ImageControllerTest implements PostIdGenerator {

    private static final String pathToController = "/api/posts/{id}/image";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageStorageService imageStorageService;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private ResourceLoader resourceLoader;

    @MockitoBean
    private Resource resource;

    @ParameterizedTest
    @MethodSource("existingPostIds")
    void updatePostImage_success(final Long id) throws Exception {

        final MediaType type = MediaType.IMAGE_PNG;
        final String imageName = "image";
        final String filePath = "image.png";

        final byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        final MockMultipartFile file = new MockMultipartFile(imageName, filePath, type.getType(), pngStub);

        final Post post = new Post( "post title", "post text", Collections.emptyList());
        post.setId(id);
        post.setImagePath(filePath);

        when(postService.findById(id)).thenReturn(post);
        when(imageStorageService.getContentType(anyString())).thenReturn(type);
        when(imageStorageService.loadImage(anyString())).thenReturn(resource);
        when(resourceLoader.getResource(any())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getContentAsByteArray()).thenReturn(pngStub);

        mockMvc.perform(multipart(pathToController, id).file(file))
                .andExpect(status().isOk());

        mockMvc.perform(get(pathToController, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(type))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void uploadImage_emptyFile_badRequest() throws Exception {
        final MockMultipartFile empty = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

        mockMvc.perform(multipart(pathToController, 1L).file(empty))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_badRequest() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("notImageName", "avatar.png", "image/png", new byte[]{1, 2, 3});
        final Long postId = 1L;
        mockMvc.perform(multipart(pathToController, postId).file(file))
                .andExpect(status().isBadRequest());
    }

}
