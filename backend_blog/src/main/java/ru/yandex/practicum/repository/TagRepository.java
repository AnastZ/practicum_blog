package ru.yandex.practicum.repository;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class TagRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DbSessionProvider sessionProvider;

    protected TagRepository(@NotNull final DbSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    /**
     * Найти теги по их названию.
     * @param names названия тегов.
     * @return найденные по названию теги.
     */
    public @NotNull List<Tag> findByNames(@NotNull final List<String> names) {
        if(Objects.isNull(names) || names.isEmpty()){
            return Collections.emptyList();
        }
        final SessionWorker<List<Tag>> worker = new SessionWorker<>();
        return worker.workWithSession(sessionProvider.getSessionFactory(), logger,
                s->{
                    return s.createNamedQuery("findByNames", Tag.class)
                            .setParameter("names", names)
                            .getResultList();
                }).orElse(Collections.emptyList());
    }

    /**
     * Сохранить все теги в списке.
     * @param tags сохраняемые теги.
     * @return сохранённые теги или пестой список.
     */
    public @NotNull List<Tag> saveAll(@NotNull final List<Tag> tags) {
        if(tags.isEmpty() || tags.stream().anyMatch(Objects::isNull)){
            return Collections.emptyList();
        }
        sessionProvider.getSessionFactory().inTransaction(s->{
            tags.forEach(t->s.persist(t));
            s.flush();
        });
        return tags;
    }
}
