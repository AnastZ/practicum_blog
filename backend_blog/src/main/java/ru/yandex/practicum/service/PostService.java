package ru.yandex.practicum.service;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.repository.Utils;
import ru.yandex.practicum.model.dto.PostDTO;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    public PostService(@NotNull final PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    /**
     * Получить количество страниц с постами относительно переданного количества постов на одной странице.
     * @param pageSize количество постов на одной странице.
     * @return количество постов относительно переданного количества на одной странице.
     */
    public long getCountPagesForSearchByTitle(@NotNull final String searchString,
                                             final int pageSize){
        final long countRecords = postRepository.getCountRecordInSearchQuery(searchString);
        return Utils.calculateCountPages(countRecords, pageSize);
    }

    /**
     * Найти посты в БД по поисковому запросу на заданной странице.
     * @param searchString поисковой запрос.
     * @param pageNumber номер страницы.
     * @param pageSize количество записей на странице.
     * @return результаты поиска в БД по поисковому запросу.
     */
    public List<PostDTO> searchAllByTitle(@NotNull final String searchString,
                                          final int pageNumber,
                                          final int pageSize) {
        return postRepository.findAllByStringQuery(searchString, pageNumber-1, pageSize).stream().toList();
    }

}
