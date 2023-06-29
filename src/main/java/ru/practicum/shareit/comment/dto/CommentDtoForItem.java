package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDtoForItem {

    private Integer id;

    private String text;

    private Integer itemId;

    private Integer authorId;

    private String authorName;

    private LocalDateTime created;
}
