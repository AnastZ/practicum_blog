package ru.yandex.practicum.repository;

import org.hibernate.SessionFactory;

public interface DbSessionProvider {
    SessionFactory getSessionFactory();
}
