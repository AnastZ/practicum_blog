package ru.yandex.practicum.unit.controllers;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.controllers.DTOMapper;
import ru.yandex.practicum.controllers.DTOToEntityMapper;
import ru.yandex.practicum.controllers.dto.InputPostDTO;
import ru.yandex.practicum.controllers.dto.PostDTO;
import ru.yandex.practicum.integration.PostIdGenerator;
import ru.yandex.practicum.models.Post;
import ru.yandex.practicum.repositories.PostRepository;
import ru.yandex.practicum.services.PostService;
import ru.yandex.practicum.services.TagService;
import ru.yandex.practicum.services.util.Merger;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PostService.class})
@AutoConfigureMockMvc
@Import(UtilConfig.class)
public class PostServiceTest implements PostIdGenerator {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private DTOMapper<Post, PostDTO> dtoMapper;
    @MockitoBean
    private DTOToEntityMapper<Post, InputPostDTO> inputPostDTOMapper;
    @MockitoBean
    private Merger<Post> postMerger;
    @MockitoBean
    private TagService tagService;

    @Test
    void searchPost_error(){
        assertThrows(IllegalArgumentException.class, ()->postService.findById(-1L));
        assertThrows(IllegalArgumentException.class, ()->postService.findById(null));
    }
    @ParameterizedTest
    @MethodSource("getPostsWithContainsA")
    void searchAllByTitle_success(final List<Post> testPosts,
                                  @Value("${post.title.short-length}") final int length) throws Exception {
        when(postRepository.findIdsByTitle(anyString(), any())).thenReturn(Page.empty());
        when(postRepository.findAllByIds(any())).thenReturn(testPosts);
        final String searchStr = "а";
        final List<PostDTO> posts = postService.searchAllByTitle(searchStr, 1, 3);
        assertTrue(posts.stream().allMatch(Objects::nonNull));
        assertTrue(posts.stream()
                .filter(p->{
                    final String title = p.title();
                    if(title.length() > length + 3 || title.isEmpty()) return true;
                    if(title.length() == length + 3 && !title.endsWith("...")) return true;
                    if(! title.contains(searchStr)) return true;
                    return false;
                })
                .filter(p->p.id() < 1L)
                .toList().isEmpty());
    }

}
