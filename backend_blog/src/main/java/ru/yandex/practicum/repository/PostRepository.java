package ru.yandex.practicum.repository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.dto.PostDTO;

import java.util.Collections;
import java.util.List;

@Repository
public class PostRepository  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DbSessionProvider sessionProvider;
    protected PostRepository(@NotNull final DbSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    /**
     * Получить количество записей поискового запроса.
     * @param searchString поисковой запрос по наименованию поста.
     * @return количество постов или 0.
     */
    @Transactional
    public long getCountRecordInSearchQuery(@NotNull final String searchString) {
        final SessionFactory sessionFactory = sessionProvider.getSessionFactory();
        if(sessionFactory.isClosed()){
            logger.error("SessionFactory is close.");
            return 1;
        }
        try(final Session session = sessionFactory.openSession()){
            final Long countRecords = session.createNamedQuery("countRecordsForSearchByTitle", Long.class)
                    .setParameter("searchString", searchString)
                    .getSingleResult();
            return countRecords;
        }catch (HibernateException he){
            logger.error(he.getMessage());
        }
        return 0;
    }

    /**
     * Получить результаты поиска по наименованию постов.
     * @param searchString строка поиска по наименованию поста.
     * @param pageNumber номер страницы, где 0 это первая страница.
     * @param pageSize количество постов на странице.
     * @return результаты поиска по наименованию постов.
     */
    @Transactional
    public @NotNull List<PostDTO> findAllByStringQuery(@NotNull final String searchString,
                                                final int pageNumber,
                                                final int pageSize){
        if(pageNumber < 0 || pageSize < 1){
            logger.warn("Переданы некорректные входные данные для поиска постов. Номер страницы (начиная с 0), переданное значение: {}. Количество записей на странице (от 1), переданное значение: {}.", pageNumber, pageSize);
            return Collections.emptyList();
        }
        final SessionFactory sessionFactory = sessionProvider.getSessionFactory();
        if(sessionFactory.isClosed()){
            logger.error("SessionFactory is close.");
            return Collections.emptyList();
        }
        try(final Session session = sessionFactory.openSession()){

            final List<PostDTO> results = session.createNamedSelectionQuery("searchByTitle", PostDTO.class)
                    .setParameter("searchString", searchString)
                    .setPage(Page.page(pageSize, pageNumber))
                    .getResultList();
            return results;
        }catch (HibernateException he){
            logger.error("Error in method {}:{}", "findAllByStringQuery", he.getMessage());
        }
        return Collections.emptyList();
    }
}

















