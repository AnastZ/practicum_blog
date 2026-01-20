package ru.practicum.blog.controllers;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleAllErrors(@NotNull final Exception e) {
        logger.error("Unexpected error: ", e);
    }
    @ExceptionHandler({NoResultException.class, NoSuchFileException.class, NonUniqueResultException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResultException(@NotNull final Exception e) {
        logger.error("Unexpected error: ", e);
    }

    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class, EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(@NotNull final IllegalArgumentException e) {
        logger.error("Unexpected error: ", e);
    }
}