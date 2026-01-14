package ru.yandex.practicum.test.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.model.dto.CommentDTO;
import ru.yandex.practicum.test.integration.IntegrationConfig;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class CommentControllerTest {

    private static final String pathToController = "/api/posts/%d/comments";

    private static String getPathForId(final long postId){
        return String.format(pathToController, postId);
    }

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }
    final static Set<String> requiredPostFields = Set.of("id", "text", "postId");

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void searchComments_ok(final long postId) throws Exception {

        final ResultActions resultActions = mockMvc.perform(get(getPathForId(postId))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        requiredPostFields.forEach(f->{
                    try {
                        resultActions.andExpect(jsonPath("$[*]." + f, everyItem(notNullValue())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    @ParameterizedTest
    @CsvSource({"1, 1",
    "2,2"})
    void searchSingleComment_ok(final long postId, final long commentId) throws Exception {

        final ResultActions resultActions = mockMvc.perform(get(getPathForId(postId)+ "/" + commentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        requiredPostFields.forEach(field->{
                    try {
                        resultActions.andExpect(jsonPath("$." + field).exists());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        resultActions.andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.postId").value(postId));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void saveComment(final long postId) throws Exception {
        final CommentDTO comment = new CommentDTO(0L, "kfkf", postId);

        final ObjectMapper mapper = new ObjectMapper();

        final ResultActions rs = mockMvc.perform(post(getPathForId(postId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(comment)))
                .andDo(print())
                .andExpect(status().isOk());
        requiredPostFields.forEach(f->{
            try {
                rs.andExpect(jsonPath("$."+f).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @ParameterizedTest
    @CsvSource({"1, 1",
            "2,2"})
    void update(final long postId, final long commentId) throws Exception {
        final CommentDTO comment = new CommentDTO(commentId, "kfkf", postId);

        final ObjectMapper mapper = new ObjectMapper();

        final ResultActions rs = mockMvc.perform(post(getPathForId(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andDo(print())
                .andExpect(status().isOk());
        requiredPostFields.forEach(f->{
            try {
                rs.andExpect(jsonPath("$."+f).exists());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        rs.andExpect(jsonPath("$.id").value(commentId))
        .andExpect(jsonPath("$.postId").value(postId));
    }
    @ParameterizedTest
    @CsvSource({"1, 1",
            "2,2"})
    void deleteComment(final long postId, final long commentId) throws Exception {
        mockMvc.perform(delete(getPathForId(postId) + "/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
