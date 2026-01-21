package ru.yandex.practicum.controllers;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.validation.constraints.NotNull;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.nio.file.NoSuchFileException;

/**
 * Обработка всех ошибок для отправки HTTP ответов.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Если возникли ошибки, которые не соответствуют обрабатываемому HTTP ответу.
     *
     * @param e
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleAllErrors(@NotNull final Exception e) {
        logger.error("Unexpected error: ", e);
    }

    /**
     * Обработка ошибок на корректные запросы, по которым не найдено данных.
     *
     * @param e
     */
    @ExceptionHandler({NoResultException.class, NoSuchFileException.class, NonUniqueResultException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResultException(@NotNull final Exception e) {
        logger.error("Not found error: ", e);
    }

    /**
     * Обработка ошибок возникших из-за некорректных запросов.
     *
     * @param e
     */
    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class, EmptyResultDataAccessException.class, ConstraintViolationException.class, MissingServletRequestPartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(@NotNull final Exception e) {
        logger.error("Bad request error: ", e);
    }
}