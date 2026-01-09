package ru.yandex.practicum.test.integration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ru.yandex.practicum.test.integration.IntegrationConfig;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
        IntegrationConfig.class,
        WebConfig.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    final static String[] requiredPostFields = {"id", "title", "text", "tags", "likesCount", "commentsCount"};

    @Test
    void searchPosts_isOk() throws Exception {
        final String path = "/api/posts";
        final String search = "111";
        final String pageNumber = "1";
        final String pageSize = "3";
        final String[] requiredPostFields = {"id", "title", "text", "tags", "likesCount", "commentsCount"};

        final ResultActions resultActions = mockMvc.perform(get(path)
                        .param("search", search)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.posts.length()").value(lessThanOrEqualTo(3)));
        Arrays.stream(requiredPostFields).forEach(field -> {
            try {
                resultActions.andExpect(jsonPath("$.posts[*]." + field).value(everyItem(notNullValue())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        final String s = resultActions
                .andExpect(jsonPath("$.posts[?(@.text.length() > 131 || @.text.length() == 0)]").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(s);

        mockMvc.perform(get(path)
                        .param("search", search)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.posts.length()").value(pageSize));
    }

@Test
    void searchSinglePosts_isOk() throws Exception {
        final String path = "/api/posts/1";

        final ResultActions rs = mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        Arrays.stream(requiredPostFields).forEach(field -> {
            try {
                rs.andExpect(jsonPath("$." + field ).value(notNullValue()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        final String s = rs
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(s);
    }
}
