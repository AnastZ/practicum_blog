package integration.controller;

import integration.PostIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.controller.ImageController;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.service.ImageService;
import ru.yandex.practicum.service.ImageStorageService;
import ru.yandex.practicum.service.PostService;
import ru.yandex.practicum.util.ValidatorConfiguration;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextHierarchy({
        @ContextConfiguration(name = "service", classes = ServiceConfiguration.class),
        @ContextConfiguration(name = "util", classes = UtilConfiguration.class)
})
public class ImageControllerTest extends AbstractControllerTest implements PostIdGenerator {

    private static final String pathToController = "/api/posts/{id}/image";

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private Resource resource;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        final ImageService imageService = new ImageService(postService, imageStorageService);
        final ImageController imageController = new ImageController(imageService, new ValidatorConfiguration().getIdValidator());
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

    }

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

        when(postService.findById(id)).thenReturn(post);
        when(imageStorageService.getContentType(anyString())).thenReturn(type);
        when(imageStorageService.loadImage(anyString())).thenReturn(resource);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getContentAsByteArray()).thenReturn(pngStub);

        mockMvc.perform(multipart(pathToController, id).file(file))
                .andExpect(status().isOk());

        post.setImagePath(filePath);

        mockMvc.perform(get(pathToController, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(type))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void uploadImage_emptyFile_badRequest() throws Exception {
        final MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

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
