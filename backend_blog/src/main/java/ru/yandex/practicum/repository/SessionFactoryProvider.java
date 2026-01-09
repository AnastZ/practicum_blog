package ru.yandex.practicum.repository;

import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.entity.Tag;


@Component
public class SessionFactoryProvider implements DbSessionProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected SessionFactoryProvider(){}

    private final SessionFactory sessionFactory = buildSessionFactory();
    /**
     * Создать sessionFactory если она ещё не создана.
     * Используется в конструкторе объекта, вызывается один раз.
     * @return созданная sessionFactory или исключение.
     * @throws ExceptionInInitializerError если произошла ошибка при создании sessionFactory.
     */
    private @NotNull SessionFactory buildSessionFactory() throws ExceptionInInitializerError{
        try {
            return new Configuration()
                    .addAnnotatedClass(Tag.class)
                    .addAnnotatedClass(Comment.class)
                    .addAnnotatedClass(Post.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Получить sessionFactory если подключение открыто.
     * @return sessionFactory в обёртке если подключение открыто или пустая обёртка.
     */
    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Закрыть sessionFactory.
     */
    public void closeSessionFactory(){
        try{
            if(sessionFactory.isOpen()){
                sessionFactory.close();
            }
        }catch (Exception ex){
            logger.error("Failed to destroy bean '{}' gracefully. Reason: {}", this.getClass(), ex.getMessage());
        }

    }
    @PreDestroy
    private void destroy(){
        closeSessionFactory();
    }
}
