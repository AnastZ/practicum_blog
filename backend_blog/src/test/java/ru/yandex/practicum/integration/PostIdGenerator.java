package ru.yandex.practicum.integration;

import org.junit.jupiter.params.provider.Arguments;
import ru.yandex.practicum.models.Post;
import ru.yandex.practicum.models.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public interface PostIdGenerator {
    static Stream<Long> existingPostIds() {
        return Stream.iterate(1L, n -> n + 1L).limit(10);
    }

    static Stream<Long> notExistingPostIds() {
        return Stream.iterate(100L, n -> n + 1L).limit(10);
    }

    static Stream<Arguments> getPostsWithContainsA() {
        return Stream.of(Arguments.of(
                List.of(
                new Post("Книга о Java", "Текст о Java", List.of(new Tag("программирование"))),
                new Post("Прогулка в парке", "Текст о парке", List.of(new Tag("отдых"))),
                new Post("Компьютерные игры", "Текст об играх", List.of(new Tag("развлечения"))),
                new Post("Анализ данных", "Текст об анализе", List.of(new Tag("аналитика"))),
                new Post(
                "Полное руководство по разработке на Java с использованием Spring Framework: от основ до продвинутых концепций включая Spring Boot, Spring Security и Spring Data JPA. Рассматриваем лучшие практики, паттерны проектирования и оптимизацию производительности для создания масштабируемых enterprise-приложений в 2024 году. Особое внимание уделяется тестированию, мониторингу и деплою в облачные среды с Docker и Kubernetes. Практические примеры и пошаговые инструкции для разработчиков всех уровней.",
                "Java - мощный язык для backend-разработки.",
                Arrays.asList(new Tag("Java"), new Tag("Spring"))),
                new Post(
                        "Глубокий анализ современных подходов к созданию микросервисной архитектуры: сравнение синхронной и асинхронной коммуникации, паттерны Saga и CQRS, управление конфигурацией, Service Discovery, Circuit Breaker и другие важные аспекты. Рассматриваем инструменты для оркестрации, мониторинга и логирования распределенных систем. Практические рекомендации по проектированию отказоустойчивых и масштабируемых систем на основе реальных кейсов из production-среды.",
                        "Микросервисы позволяют лучше масштабировать приложения.",
                        Arrays.asList(new Tag("Microservices"), new Tag("Architecture"))),
                new Post(
                        "Исчерпывающее руководство по работе с базами данных в Java-приложениях: сравнение SQL и NoSQL решений, оптимизация запросов, индексация, транзакции и управление подключениями. Рассматриваем ORM фреймворки Hibernate и JPA, их преимущества и недостатки. Паттерны доступа к данным, кэширование, репликация и шардирование. Особое внимание уделяется производительности и безопасности баз данных в высоконагруженных системах с миллионами запросов в день.",
                        "Базы данных - критически важный компонент любого приложения.",
                        Arrays.asList(new Tag("Database"), new Tag("Hibernate"))),
                new Post(
                        "Комплексный обзор инструментов DevOps для Java-разработчиков: непрерывная интеграция и доставка с Jenkins и GitLab CI, контейнеризация с Docker, оркестрация с Kubernetes, инфраструктура как код с Terraform. Рассматриваем мониторинг с Prometheus и Grafana, логирование с ELK Stack, управление секретами и конфигурацией. Практические примеры настройки пайплайнов деплоя, blue-green deployments, canary releases и стратегии обновления без downtime.",
                        "DevOps ускоряет процесс разработки и доставки ПО.",
                        Arrays.asList(new Tag("DevOps"), new Tag("Docker"), new Tag("Kubernetes"))),
                new Post(
                        "Детальный анализ фреймворков для тестирования Java-приложений: JUnit 5, Mockito, Testcontainers, Selenium, Cucumber. Рассматриваем различные виды тестирования - unit, integration, system, end-to-end. Паттерны написания чистых и поддерживаемых тестов, тестовое покрытие, mutation testing. Особое внимание уделяется тестированию Spring приложений, работе с базами данных в тестах, мокированию внешних сервисов. Практические примеры и рекомендации по организации тестовой инфраструктуры.",
                        "Качественное тестирование - залог надежности приложения.",
                        Arrays.asList(new Tag("Testing"), new Tag("JUnit"))),
                new Post(
                        "Полное руководство по безопасности Java-приложений: защита от OWASP Top 10 уязвимостей, аутентификация и авторизация с Spring Security, JWT токены, OAuth2, OpenID Connect. Рассматриваем шифрование данных, безопасную работу с паролями, защиту от SQL-инъекций и XSS атак. Практические рекомендации по настройке HTTPS, управлению сертификатами, аудиту безопасности и compliance требованиям для enterprise-приложений в финансовом и медицинском секторах.",
                        "Безопасность должна быть встроена в процесс разработки.",
                        Arrays.asList(new Tag("Security"), new Tag("Spring Security"))),
                new Post(
                        "Современные подходы к разработке RESTful API на Java: дизайн ресурсов, версионирование, документация с OpenAPI и Swagger, пагинация, фильтрация, сортировка. Рассматриваем GraphQL как альтернативу REST, сравнение подходов, производительность и гибкость. Особое внимание уделяется кэшированию с Redis, rate limiting, мониторингу API, метрикам и аналитике использования. Практические примеры создания отказоустойчивых и высокопроизводительных API для мобильных и веб-приложений.",
                        "API - это интерфейс взаимодействия между системами.",
                        Arrays.asList(new Tag("API"), new Tag("REST"), new Tag("GraphQL"))),
                new Post(
                        "Основы Java",
                        "Введение в язык программирования Java и его основные концепции.",
                        Arrays.asList(new Tag("Java"), new Tag("Basics"))),
                new Post(
                        "Spring Boot быстро",
                        "Создание первого приложения на Spring Boot за 15 минут.",
                        Arrays.asList(new Tag("Spring"), new Tag("Quickstart"))),
                new Post(
                        "Базы данных SQL",
                        "Основы реляционных баз данных и язык запросов SQL.",
                        Arrays.asList(new Tag("SQL"), new Tag("Database"))),
                new Post(
                        "Docker для разработчиков",
                        "Основы работы с контейнерами Docker в разработке.",
                        Arrays.asList(new Tag("Docker"), new Tag("Containers"))),
                new Post(
                        "Тестирование с JUnit",
                        "Написание unit-тестов с использованием JUnit 5.",
                        Arrays.asList(new Tag("Testing"), new Tag("JUnit"))),
                new Post(
                        "Git и GitHub",
                        "Система контроля версий Git и работа с GitHub.",
                        Arrays.asList(new Tag("Git"), new Tag("Version Control"))),
                new Post(
                        "Архитектура ПО",
                        "Основные принципы проектирования архитектуры приложений.",
                        Arrays.asList(new Tag("Architecture"), new Tag("Design"))))));
    }
}
