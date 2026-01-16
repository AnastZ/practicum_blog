package ru.yandex.practicum.repository;


import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.validation.constraints.NotNull;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.util.EntityValidator;

import java.util.*;

@Repository
public class PostRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SessionFactory sessionFactory;
    private final EntityValidator<Long> idValidator;

    public PostRepository(@NotNull final SessionFactory sessionFactory,
                          @NotNull final EntityValidator<Long> idValidator) {
        this.sessionFactory = sessionFactory;
        this.idValidator = idValidator;
    }

    /** Вспомогательный метод для получения текущей сессии
    **/
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Получить количество записей поискового запроса.
     *
     * @param searchString поисковой запрос по наименованию поста.
     * @return количество постов или 0.
     */
    @Transactional(readOnly = true)
    public long getCountRecordInSearchQuery(@NotNull final String searchString) {
        return getCurrentSession().createNamedQuery("countRecordsForSearchByTitle", Long.class)
                .setParameter("searchString", searchString)
                .getSingleResult();
    }

    /**
     * Получить результаты поиска по наименованию постов.
     *
     * @param searchString строка поиска по наименованию поста.
     * @param pageNumber   номер страницы, где 0 это первая страница.
     * @param pageSize     количество постов на странице.
     * @return результаты поиска по наименованию постов.
     */
    @Transactional(readOnly = true)
    public @NotNull List<Post> findAllByStringQuery(@NotNull final String searchString,
                                                    final int pageNumber,
                                                    final int pageSize) throws Exception{
        if (pageNumber < 0 || pageSize < 1) {
            throw new IllegalArgumentException("Переданы некорректные входные данные для поиска постов.");
        }

        final List<Long> postIds = getCurrentSession().createNamedQuery("getIdsForSearchByTitle", Long.class)
                .setParameter("searchString", searchString)
                .setPage(Page.page(pageSize, pageNumber))
                .getResultList();
        if(postIds.isEmpty()){
            return Collections.emptyList();
        }
        final List<Post> results = getCurrentSession().createNamedSelectionQuery("searchByTitle", Post.class)
                .setParameter("ids", postIds)
                .getResultList();
        return results;
    }
    /**
     * Получить пост из БД по его уникальному номеру.
     *
     * @param id уникальный номер поста.
     * @return найденный пост.
     */
    @Transactional(readOnly = true)
    public @NotNull Post findById(@NotNull final Long id) throws NoResultException, NonUniqueResultException {
        idValidator.validate(id);
        return getCurrentSession().createNamedQuery("findSinglePost", Post.class)
                .setCacheable(false)
                .setParameter("id", id).getSingleResult();
    }

    /**
     * Сохранение нового поста.
     * @param post сохраняемый пост.
     * @return новый объект, полученный из метода merge(post).
     */
    @Transactional
    public Post save(@NotNull final Post post) throws Exception {
        return getCurrentSession().merge(post);
    }

    /**
     * Удалить пост из БД по его уникальному номеру.
     * @param postId уникальный номер поста.
     * @throws Exception если пост с переданным уникальным номером не существует в БД или другие ошибки при работе с БД.
     */
    @Transactional
    public void delete(@NotNull final Long postId) throws Exception {
        idValidator.validate(postId);
        final Session session = getCurrentSession();
        final Post p = session.find(Post.class, postId);
        if(Objects.isNull(p)){
            throw new NoResultException("Удаляемого объекта не существует.");
        }
        session.remove(p);
    }

    /**
     * Инкремент количества лайков для поста с переданным уникальным номером.
     * @param postId уникальный номер поста.
     * @return инкрементированное количество постов.
     * @throws Exception
     */
    @Transactional
    public Long incrementLikes(@NotNull final Long postId) throws Exception {
        idValidator.validate(postId);
        final Session session = getCurrentSession();
        session.createNamedMutationQuery("incrementLikes")
                .setParameter("id", postId)
                .executeUpdate();
        session.flush();
        return session.createNamedQuery("getCountLikes",  Long.class)
                .setParameter("id", postId)
                .getSingleResult();
    }

    /**
     * Обновить путь к изображению поста.
     * @param postId уникальный номер поста.
     * @param imagePath новый путь к картинке.
     * @throws Exception
     */
    @Transactional
    public void updatePostImagePath(@NotNull final Long postId,
                                    @NotNull final String imagePath) throws Exception {
        idValidator.validate(postId);
        final Session session = getCurrentSession();
        final Post p = session.find(Post.class, postId);
        if(Objects.isNull(p)){
            throw new NoResultException("Поста не существует.");
        }
        p.setImagePath(imagePath);
        session.persist(p);
        session.flush();
    }


}















