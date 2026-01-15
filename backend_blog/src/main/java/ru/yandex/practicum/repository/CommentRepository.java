package ru.yandex.practicum.repository;

import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.util.EntityValidator;

import java.util.List;
import java.util.Objects;

@Repository
public class CommentRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SessionFactory sessionFactory;
    private final EntityValidator<Long> idValidator;

    public CommentRepository(@NotNull final SessionFactory sessionFactory,
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
     * Получить список комментариев, оносящиеся к посту.
     * @param postId уникальный номер поста.
     * @return список комментариев поста.
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(final Long postId) throws Exception {
        idValidator.validate(postId);
        return getCurrentSession().createNamedQuery("getCommentsByPostId", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    /**
     * Получить комментарий по его id, который относится к переданному id поста.
     * @param commentId уникальный номер комментария.
     * @param postId уникальный номер поста.
     * @return комментарий поста.
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Comment getCommentByIdAndPostId(final Long commentId, final Long postId) throws Exception {
        idValidator.validate(commentId);
        idValidator.validate(postId);
        return getCurrentSession().createNamedQuery("getCommentByIdAndPostId", Comment.class)
                .setParameter("commentId", commentId)
                .setParameter("postId", postId)
                .getSingleResult();

    }

    /**
     * Сохранить комментарий.
     * @param comment комментарий.
     * @return новый объект комментария с присвоенным уникальным номером.
     */
    @Transactional
    public Comment save(@NotNull final Comment comment) {
        return getCurrentSession().merge(comment);
    }

    /**
     * Удалить комментарий если он относится к уникальному номеру поста.
     * @param postId уникальный номер поста.
     * @param commentId уникальный номер комментария.
     * @throws Exception
     */
    @Transactional
    public void delete(@NotNull final Long postId,
                       @NotNull final Long commentId) throws Exception{
        idValidator.validate(commentId);
        final Session session = getCurrentSession();
        final Comment comment = session.find(Comment.class, commentId);
        if(Objects.isNull(comment)){
            throw new NoResultException("Удаляемого объекта не существует.");
        }
        if(! comment.getPost().getId().equals(postId)){
            throw new IllegalArgumentException("Комментарий не может быть удалён, так как принадлежит другому посту.");
        }
        session.remove(comment);
        session.flush();
        session.clear();
    }
}
