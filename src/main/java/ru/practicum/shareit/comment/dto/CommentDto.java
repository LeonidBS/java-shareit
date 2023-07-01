package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {

    private Integer id;

    private String text;

    private Integer itemId;

    private String itemName;

    private Integer authorId;

    private String authorName;

    private LocalDateTime created;
}
