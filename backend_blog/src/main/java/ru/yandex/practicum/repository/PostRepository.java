package ru.yandex.practicum.repository;

import jakarta.transaction.Transactional;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.dto.PostDTO;
import ru.yandex.practicum.model.entity.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PostRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DbSessionProvider sessionProvider;

    protected PostRepository(@NotNull final DbSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    /**
     * Получить количество записей поискового запроса.
     *
     * @param searchString поисковой запрос по наименованию поста.
     * @return количество постов или 0.
     */
    @Transactional
    public long getCountRecordInSearchQuery(@NotNull final String searchString) {
        final SessionWorker<Long> worker = new SessionWorker<Long>();
        final Optional<Long> result = worker.workWithSession(sessionProvider.getSessionFactory(), logger,
                s->{
                    final Long countRecords = s.createNamedQuery("countRecordsForSearchByTitle", Long.class)
                            .setParameter("searchString", searchString)
                            .getSingleResult();
                    return countRecords;
                });
        return result.orElse(0L);
    }

    /**
     * Получить результаты поиска по наименованию постов.
     *
     * @param searchString строка поиска по наименованию поста.
     * @param pageNumber   номер страницы, где 0 это первая страница.
     * @param pageSize     количество постов на странице.
     * @return результаты поиска по наименованию постов.
     */
    @Transactional
    public @NotNull List<Post> findAllByStringQuery(@NotNull final String searchString,
                                                    final int pageNumber,
                                                    final int pageSize) {
        if (pageNumber < 0 || pageSize < 1) {
            logger.warn("Переданы некорректные входные данные для поиска постов. Номер страницы (начиная с 0), переданное значение: {}. Количество записей на странице (от 1), переданное значение: {}.", pageNumber, pageSize);
            return Collections.emptyList();
        }
        final SessionWorker<List<Post>> sessionWorker = new SessionWorker<>();
        final Optional<List<Post>> posts = sessionWorker.workWithSession(sessionProvider.getSessionFactory(),
                logger,
                (session) -> {
                    final List<Long> postIds = session.createNamedQuery("getIdsForSearchByTitle", Long.class)
                            .setParameter("searchString", searchString)
                            .setPage(Page.page(pageSize, pageNumber))
                            .getResultList();
                    final List<Post> results = session.createNamedSelectionQuery("searchByTitle", Post.class)
                            .setParameter("ids", postIds)
                            .getResultList();
                    return results;
                });
        return posts.orElse(Collections.emptyList());
    }

    /**
     * Получить пост из БД по его уникальному номеру.
     *
     * @param id уникальный номер поста.
     * @return найденный пост.
     */
    public @NotNull Optional<Post> findById(@NotNull final Long id) {
        if (id < 1) {
            logger.warn("Передан некорректный уникальный номер для поиска в БД. Переданное значение: {}", id);
            return Optional.empty();
        }
        final SessionWorker<Post> worker = new SessionWorker<>();
        return worker.workWithSession(sessionProvider.getSessionFactory(),
                logger,
                (session) -> {
                    return session.createNamedQuery("findSinglePost", Post.class)
                            .setParameter("id", id).getSingleResult();
                });
    }

    public Post save(@NotNull final Post post) {

        return sessionProvider.getSessionFactory().fromTransaction(s->{
            final Post p = s.merge(post);
            s.flush();
            return p;
        });
       /* try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
            final Validator validator = factory.getValidator();
            final Set<ConstraintViolation<Post>> violations = validator.validate(post);
            if(! violations.isEmpty()){
                violations.forEach(violation -> logger.warn(violation.getMessage()));
                return Optional.empty();
            }
            sessionProvider.getSessionFactory().inTransaction(s->{
                s.persist(post);
            });
            return Optional.of(post);
        }catch (ValidationException e){
            logger.error(e.getMessage());
        }
        return Optional.empty();*/
    }
}

















