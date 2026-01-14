package ru.yandex.practicum.test.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
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
import ru.yandex.practicum.model.dto.InputPostDTO;
import ru.yandex.practicum.model.dto.UpdatingPostDTO;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.test.integration.IntegrationConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class PostControllerTest {
    private static final String pathToController = "/api/posts";

    private static String getPathForId(final long id){
        return pathToController + "/" + id;
    }

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }

    final static Set<String> requiredPostFields = Set.of("id", "title", "text", "tags", "likesCount", "commentsCount");

    /**
     * Проверка, что возвращаемый статус ответа 200.
     *
     * @param search     поисковой запрос по наименованию поста.
     * @param pageNumber номер страницы.
     * @param pageSize   количество постов на странице.
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
        requiredPostFields.forEach(field -> {
            try {
                resultActions.andExpect(jsonPath("$.posts[*]." + field).value(everyItem(notNullValue())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Поиск поста но уникальному номеру.
     *
     * @param postId уникальный номер поста.
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void searchSinglePosts_isOk(final int postId) throws Exception {

        final ResultActions rs = searchSinglePost(postId).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        requiredPostFields.forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Создание обращения к контроллеру для поиска одного поста по уникальному номеру.
     * Создание GET запроса, MediaType.APPLICATION_JSON и печать запроса в консоль.
     * @param postId уникальный номер поста.
     * @return
     * @throws Exception
     */
    private ResultActions searchSinglePost(final int postId) throws Exception {


        final ResultActions rs = mockMvc.perform(get(getPathForId(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print());
        return rs;
    }

    /**
     * Сохранение поста.
     *
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
                .content(mapper.writeValueAsString(post)));
        testSaveOrUpdatePost(rs, post)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    /**
     * Обновление поста.
     *
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void updatePost_isOk(final long id) throws Exception {
        final UpdatingPostDTO post = new UpdatingPostDTO(id,
                "Название поста 3",
                "Текст поста в формате Markdown...",
                List.of("tag1", "tag2"));
        final ObjectMapper mapper = new ObjectMapper();

        final ResultActions rs = mockMvc.perform(put(getPathForId(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post)));
        testSaveOrUpdatePost(rs, post)
                .andExpect(status().isOk());
    }
    /**
     * Обновление поста. Разные уникальные номера в пути и объекте, должна быть ошибка.
     *
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void updatePost_error(final long id) throws Exception {
        final UpdatingPostDTO post = new UpdatingPostDTO(id+1,
                "Название поста 3",
                "Текст поста в формате Markdown...",
                List.of("tag1", "tag2"));
        final ObjectMapper mapper = new ObjectMapper();

        final ResultActions rs = mockMvc.perform(put(getPathForId(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post)))
                .andExpect(status().isBadRequest());
    }

    private ResultActions testSaveOrUpdatePost(@NotNull final ResultActions rs,
                                               @NotNull final InputPostDTO post) throws Exception {
        rs.andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        requiredPostFields.forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        rs.andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.text").value(post.getText()))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(post.getTags().size()))
                .andExpect(jsonPath("$.tags", containsInAnyOrder(post.getTags().toArray())));
        return rs;
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void deletePost_isOk(final int id) throws Exception {
        mockMvc.perform(delete(getPathForId(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void incrementLikes_isOk(final int id) throws Exception {

        final String findPost = mockMvc.perform(get(getPathForId(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();
        final Post post = mapper.readValue(findPost, Post.class);
        assertNotNull(post);
        mockMvc.perform(post(getPathForId(id) + "/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(post.getLikesCount()+1));
    }
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void incrementLikes_errorPostNotFound(final int id) throws Exception {

        final String findPost = mockMvc.perform(get(getPathForId(id)  + "/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(content().string(notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertFalse(findPost.isEmpty());
        final ObjectMapper mapper = new ObjectMapper();
        final Post post = mapper.readValue(findPost, Post.class);
        assertNotNull(post);
        mockMvc.perform(post(getPathForId(id)  + "/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(post.getLikesCount()+1));
    }



}
