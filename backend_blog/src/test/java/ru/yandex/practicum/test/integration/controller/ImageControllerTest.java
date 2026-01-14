package ru.yandex.practicum.test.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.WebConfig;
import ru.yandex.practicum.controller.ImageController;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.service.ImageService;
import ru.yandex.practicum.service.ImageStorageService;
import ru.yandex.practicum.service.PostService;
import ru.yandex.practicum.test.integration.IntegrationConfig;
import ru.yandex.practicum.util.ValidatorConfiguration;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class ImageControllerTest {

    private final String pathToController = "/api/posts/%d/image";

    @Mock
    private ImageController imageController;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @Mock
    private PostService postService;

    @Mock
    private ImageStorageService imageStorageService;

    private ImageService imageService;


    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с моками репозитория и лоадера
        imageService = new ImageService(postService, imageStorageService);
        // Создаем контроллер с реальным сервисом
        final ImageController imageController = new ImageController(imageService, new ValidatorConfiguration().getIdValidator());
        // Инициализируем MockMvc для этого контроллера
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void updatePostImage_isOk(final int id) throws Exception {
        final String path = String.format(pathToController, id);
        MockMultipartFile file
                = new MockMultipartFile(
                "image",
                "D:\\test.png",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc.perform(multipart(path).file(file))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void getImage_ok(final long postId) throws Exception {
        final String path = String.format(pathToController, postId);
        final Post post = new Post( "eger", "fggg", Collections.emptyList());
        post.setId(postId);
        post.setImagePath("image.jpg");
        final byte[] dummyImage = new byte[]{1, 2, 3, 4};

        when(postService.findById(postId)).thenReturn(post);
        when(imageStorageService.getContentType(anyString())).thenReturn(MediaType.IMAGE_JPEG);
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getContentAsByteArray()).thenReturn(dummyImage);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(dummyImage));
    }
}
