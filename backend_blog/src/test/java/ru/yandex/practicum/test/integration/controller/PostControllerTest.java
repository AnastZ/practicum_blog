package ru.yandex.practicum.test.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.WebConfig;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.test.integration.IntegrationConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerTest {
    private final String pathToController = "/api/posts";

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    final static String[] requiredPostFields = {"id", "title", "text", "tags", "likesCount", "commentsCount"};

    /**
     * Проверка, что возвращаемый статус ответа 200.
     *
     * @param search поисковой запрос по наименованию поста.
     * @param pageNumber номер страницы.
     * @param pageSize количество постов на странице.
     * @throws Exception
     */
    @ParameterizedTest
    @CsvSource({
            "111, 1, 3"
    })
    void searchPosts_isOk(final Integer search,
                          final Integer pageNumber,
                          final Integer pageSize) throws Exception {

        final ResultActions resultActions = mockMvc.perform(get(pathToController)
                        .param("search", search.toString())
                        .param("pageNumber", pageNumber.toString())
                        .param("pageSize", pageSize.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").exists())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.hasPrev").exists())
                .andExpect(jsonPath("$.hasNext").exists())
                .andExpect(jsonPath("$.lastPage").exists())
                .andExpect(jsonPath("$.hasPrev").value(pageNumber > 1))
                .andExpect(jsonPath("$.hasNext").value(pageNumber < pageSize))
                .andExpect(jsonPath("$.lastPage").value(greaterThanOrEqualTo(pageNumber)))
                .andExpect(jsonPath("$.posts.length()").value(lessThanOrEqualTo(pageSize)))
                .andExpect(jsonPath("$.posts[?(@.text.length() > 131)]").isEmpty())
                .andExpect(jsonPath("$[?(" +
                        "(@.hasNext == true && @.posts.length() == " + pageSize + ") || " +
                        "(@.hasNext == false && @.posts.length() <= " + pageSize + ")" +
                        ")]").exists());
        Arrays.stream(requiredPostFields).forEach(field -> {
            try {
                resultActions.andExpect(jsonPath("$.posts[*]." + field).value(everyItem(notNullValue())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Поиск поста но уникальному номеру.
     * @param postId уникальный номер поста.
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(ints =  {1, 2, 3, 4, 5, 6, 7})
    void searchSinglePosts_isOk(final int postId) throws Exception {
        final String path = pathToController + postId;

        final ResultActions rs = mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        Arrays.stream(requiredPostFields).forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field ).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Сохранение поста.
     * @throws Exception
     */
    @Test
    void savePost_isOk() throws Exception {
        final AddingPostDTO post = new AddingPostDTO("Название поста 3",
                "Текст поста в формате Markdown...",
                List.of("tag1", "tag2"));
        final ObjectMapper mapper = new ObjectMapper();

        final ResultActions rs = mockMvc.perform(post(pathToController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        Arrays.stream(requiredPostFields).forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field ).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        rs.andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0))
                .andExpect(jsonPath("$.title").value(post.title()))
                .andExpect(jsonPath("$.text").value(post.text()))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(post.tags().size()))
                .andExpect(jsonPath("$.tags", containsInAnyOrder(post.tags().toArray())));
    }
}
