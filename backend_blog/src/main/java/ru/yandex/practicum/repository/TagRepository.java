package ru.yandex.practicum.repository;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class TagRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SessionFactory sessionFactory;


    public TagRepository(@NotNull final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** Вспомогательный метод для получения текущей сессии
     **/
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }


    /**
     * Найти теги по их названию.
     * @param names названия тегов.
     * @return найденные по названию теги.
     */
    @Transactional(readOnly = true)
    public @NotNull List<Tag> findByNames(@NotNull final List<String> names) throws Exception {
        if(Objects.isNull(names) || names.isEmpty()){
            return Collections.emptyList();
        }
        var tags = getCurrentSession().createNamedQuery("findByNames", Tag.class)
                            .setParameter("names", names)
                            .getResultList();
        return tags;
    }

    /**
     * Сохранить все теги в списке.
     * @param tags сохраняемые теги.
     * @return сохранённые теги или пестой список.
     */
    @Transactional
    public @NotNull List<Tag> saveAll(@NotNull final List<Tag> tags) throws Exception{
        if(tags.isEmpty() || tags.stream().anyMatch(Objects::isNull)){
            return Collections.emptyList();
        }
        final Session session = getCurrentSession();
        tags.forEach(session::persist);
        session.flush();
        return tags;
    }
}
