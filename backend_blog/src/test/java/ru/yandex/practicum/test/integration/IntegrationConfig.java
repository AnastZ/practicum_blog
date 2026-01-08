package ru.yandex.practicum.test.integration;


import jakarta.validation.constraints.NotNull;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.DbSessionProvider;

@Configuration
@ComponentScan(basePackages = {"ru.yandex.practicum"})
public class IntegrationConfig {


    /*@Bean
    @Primary
    public DbSessionProvider getDbSessionProvider() {
        return new DbSessionProvider() {

            private final SessionFactory sessionFactory = buildSessionFactory();
            *//**
             * Создать sessionFactory если она ещё не создана.
             * Используется в конструкторе объекта, вызывается один раз.
             * @return созданная sessionFactory или исключение.
             * @throws ExceptionInInitializerError если произошла ошибка при создании sessionFactory.
             *//*
            private @NotNull SessionFactory buildSessionFactory() throws ExceptionInInitializerError{
                try {
                    return new org.hibernate.cfg.Configuration()
                            .addAnnotatedClass(Comment.class)
                            .addAnnotatedClass(Post.class)
                            .buildSessionFactory();
                } catch (Throwable ex) {
                    throw new ExceptionInInitializerError(ex);
                }
            }
            @Override
            public SessionFactory getSessionFactory() {
                return sessionFactory;
            }
        };
    }*/
}
