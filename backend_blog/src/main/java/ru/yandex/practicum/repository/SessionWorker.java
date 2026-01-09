package ru.yandex.practicum.repository;


import jakarta.validation.constraints.NotNull;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class SessionWorker<T> {

    /**
     * Шаблонный код по открытию и закрытию сессии.
     * Обработка случая, если переданная sessionFactory закрыта, обработка HibernateException при работе с сессией.
     * Сессия в блоке autocloseable, закрывать сессию в блоке Function<Session, T> не нужно.
     * Ошибки выводятся в переданный объект логирования.
     * @param sessionFactory фабрика сессий для обращения к БД.
     * @param logger объект логирования.
     * @param workWithSession работа, проводимая с сессией.
     * @return результат работы переданной функции Function<Session, T>.
     */
    public Optional<T> workWithSession(@NotNull final SessionFactory sessionFactory,
                                       @NotNull final Logger logger,
                                       @NotNull final Function<Session, T> workWithSession){
        if(sessionFactory.isClosed()){
            logger.error("SessionFactory is close.");
            return Optional.empty();
        }
        try(final Session session = sessionFactory.openSession()){
            return Optional.ofNullable(workWithSession.apply(session));
        }catch (HibernateException he){
            logger.error("Error in method {}:{}", "findAllByStringQuery", he.getMessage());
        }
        return Optional.empty();
    }
}
