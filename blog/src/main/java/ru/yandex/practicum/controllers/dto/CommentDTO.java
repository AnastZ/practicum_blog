package ru.practicum.blog.controllers.dto;

public record CommentDTO(Long id, String text, Long postId) {
}
