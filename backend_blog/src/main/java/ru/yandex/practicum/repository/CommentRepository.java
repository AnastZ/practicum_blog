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
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(final Long id) throws Exception {
        idValidator.validate(id);
        return getCurrentSession().createNamedQuery("getCommentsByPostId", Comment.class)
                .setParameter("postId", id)
                .getResultList();
    }
    @Transactional(readOnly = true)
    public Comment getCommentByIdAndPostId(final Long commentId, final Long postId) throws Exception {
        idValidator.validate(commentId);
        idValidator.validate(postId);
        return getCurrentSession().createNamedQuery("getCommentByIdAndPostId", Comment.class)
                .setParameter("commentId", commentId)
                .setParameter("postId", postId)
                .getSingleResult();

    }
    @Transactional
    public Comment save(Comment comment) {
        return getCurrentSession().merge(comment);
    }
    @Transactional
    public void delete(Long id) throws Exception{
        idValidator.validate(id);
        final Session session = getCurrentSession();
        final Comment comment = session.find(Comment.class, id);
        if(Objects.isNull(comment)){
            throw new NoResultException("Удаляемого объекта не существует.");
        }
        session.remove(comment);
        session.flush();
        session.clear();
    }
}
