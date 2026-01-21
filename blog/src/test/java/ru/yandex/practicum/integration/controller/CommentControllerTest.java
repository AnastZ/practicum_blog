package ru.yandex.practicum.integration.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.controllers.dto.CommentDTO;
import ru.yandex.practicum.integration.AbstractRepositoryTest;
import ru.yandex.practicum.integration.CommentGenerator;
import tools.jackson.databind.ObjectMapper;


import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class CommentControllerTest extends AbstractRepositoryTest implements CommentGenerator {

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
    @MethodSource("existingPostAndComments")
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
    @MethodSource("existingPostAndComments")
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
    @MethodSource("notCorrespondingPostAndComment")
    void update_error(final long postId, final long commentId) throws Exception {
        final CommentDTO comment = new CommentDTO(commentId, "kfkf", postId);
        final ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post(getPathForId(postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @ParameterizedTest
    @MethodSource("existingPostAndComments")
    void deleteComment(final long postId, final long commentId) throws Exception {
        mockMvc.perform(delete(getPathForId(postId) + "/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
