package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import integration.PostIdGenerator;
import integration.AbstractRepositoryTest;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.model.dto.AddingPostDTO;
import ru.yandex.practicum.model.dto.InputPostDTO;
import ru.yandex.practicum.model.dto.UpdatingPostDTO;
import ru.yandex.practicum.model.entity.Post;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class PostControllerTest extends AbstractRepositoryTest implements PostIdGenerator {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }

    final static Set<String> requiredPostFields = Set.of("id", "title", "text", "tags", "likesCount", "commentsCount");
    private static final String pathToController = "/api/posts";

    private static String getPathForId(final long id) {
        return pathToController + "/" + id;
    }



    /**
     * Проверка, что возвращённые данные не противоречат условиям.
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
    void searchPosts_success(final Integer search,
                             final Integer pageNumber,
                             final Integer pageSize) throws Exception {

        final ResultActions resultActions = mockMvc.perform(get(pathToController)
                        .param("search", search.toString())
                        .param("pageNumber", pageNumber.toString())
                        .param("pageSize", pageSize.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
    @MethodSource("testExistingPostIds")
    void searchSinglePosts_success(final Long postId) throws Exception {

        final ResultActions rs = mockMvc.perform(get(getPathForId(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId));
        requiredPostFields.forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @ParameterizedTest
    @MethodSource("testNotExistingPostIds")
    void searchSinglePosts_notSuccess(final Long postId) throws Exception {

        final ResultActions rs = mockMvc.perform(get(getPathForId(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    /**
     * Одинаковая часть кода при тестировании сохранения нового поста и обновления существующего.
     * @param rs объект тестирования.
     * @param post отправленный пост.
     * @return тот же объект тестирования.
     * @throws Exception
     */
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
    /**
     * Сохранение поста.
     *
     * @throws Exception
     */
    @Test
    void savePost_success() throws Exception {
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
    @MethodSource("testExistingPostIds")
    void updatePost_isOk(final Long id) throws Exception {
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
    @MethodSource("testExistingPostIds")
    void updatePost_error(final Long id) throws Exception {
        final UpdatingPostDTO post = new UpdatingPostDTO(id + 1,
                "Название поста 3",
                "Текст поста в формате Markdown...",
                List.of("tag1", "tag2"));
        final ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(put(getPathForId(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(post)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Удаление поста.
     * @param id уникальный номер поста.
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("testExistingPostIds")
    void deletePost_success(final Long id) throws Exception {
        mockMvc.perform(delete(getPathForId(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        searchSinglePosts_notSuccess(id);
    }

    /**
     * Увеличение количества лайков поста на единицу. Успешный случай, когда пост найден в БД.
     * @param id уникальный номер поста.
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("testExistingPostIds")
    void incrementLikes_success(final Long id) throws Exception {

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
                .andExpect(jsonPath("$").value(post.getLikesCount() + 1));
    }

    /**
     * Увеличение количества лайков поста на единицу. Случай с ошибкой, когда пост не найден в БД.
     * @param id
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("testNotExistingPostIds")
    void incrementLikes_errorPostNotFound(final Long id) throws Exception {
        mockMvc.perform(post(getPathForId(id) + "/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
