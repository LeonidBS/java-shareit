package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getItem().getName(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
