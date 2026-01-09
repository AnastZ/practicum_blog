-- Очистка таблиц перед заполнением (необязательно, но полезно для тестов)
SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE post_tags;
TRUNCATE TABLE comment;
TRUNCATE TABLE tag;
TRUNCATE TABLE post;
SET REFERENTIAL_INTEGRITY TRUE;

-- 1. Наполнение таблицы POST (10 записей)
INSERT INTO post (title, text, likes_count, created_date) VALUES
                                                              ('Введение в Hibernate            1', 'Подробный разбор ORM для начинающих.', 15, '2024-01-10 10:00:00'),
                                                              ('Spring Boot 3.0                 111', 'Что нового в последней версии фреймворка?', 25, '2024-01-12 11:30:00'),
                                                              ('Работа с MySQL                  111', 'Оптимизация запросов и индексы.', 10, '2024-01-15 09:15:00'),
                                                              ('Настройка Apache24              11111', 'Как подружить Apache и PHP на Windows.', 5, '2024-01-18 14:00:00'),
                                                              ('Основы Java Records             11111', 'Зачем нужны рекорды и как их использовать.', 40, '2024-01-20 16:45:00'),
                                                              ('Базы данных H2                  11111', 'Использование In-Memory БД для тестирования.', 12, '2024-01-22 12:00:00'),
                                                              ('Docker для новичков             111111', 'Контейнеризация вашего приложения.', 30, '2024-01-25 08:00:00'),
                                                              ('Микросервисы vs Монолит         111111', 'Архитектурные подходы в 2024 году.ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo', 18, '2024-01-27 15:20:00'),
                                                              ('Безопасность Spring Security    1111111111', 'Защита вашего REST API.', 22, '2024-01-30 10:10:00'),
                                                              ('Алгоритмы на Java               1111111111', 'Разбор задач для собеседования.', 50, '2024-02-01 18:00:00');

-- 2. Наполнение таблицы COMMENT (по 1 комментарию на каждый пост)
INSERT INTO comment (text, idpost) VALUES
                                       ('Очень полезная статья, спасибо!', 1),
                                       ('Ждал этого обновления!', 2),
                                       ('А будут примеры с PostgreSQL?', 3),
                                       ('У меня возникла ошибка при установке...', 4),
                                       ('Рекорды — это лучшее, что было в Java 16.', 5),
                                       ('H2 идеален для Unit-тестов.', 6),
                                       ('Можно подробнее про Docker Compose?', 7),
                                       ('Спорный вопрос про микросервисы.', 8),
                                       ('Security — это всегда сложно.', 9),
                                       ('Задача про бинарное дерево была классной.', 10);

-- 3. Наполнение таблицы TAG (10 тегов)
INSERT INTO tag (tag_name) VALUES
                               ('Java'), ('Spring'), ('Database'), ('Hibernate'), ('Backend'),
                               ('DevOps'), ('Tutorial'), ('Architecture'), ('Security'), ('SQL');

-- 4. Наполнение таблицы POST_TAGS (связи между постами и тегами)
INSERT INTO post_tags (idpost, idtag) VALUES
                                          (1, 1), (1, 4), -- Пост 1: Java, Hibernate
                                          (2, 2), (2, 5), -- Пост 2: Spring, Backend
                                          (3, 3), (3, 10), -- Пост 3: Database, SQL
                                          (4, 7), (4, 5), -- Пост 4: Tutorial, Backend
                                          (5, 1), (5, 7), -- Пост 5: Java, Tutorial
                                          (6, 3), (6, 1), -- Пост 6: Database, Java
                                          (7, 6), (7, 5), -- Пост 7: DevOps, Backend
                                          (8, 8), (8, 5), -- Пост 8: Architecture, Backend
                                          (9, 9), (9, 2), -- Пост 9: Security, Spring
                                          (10, 1), (10, 7); -- Пост 10: Java, Tutorial
