package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoInput {

    private Integer id;

    private String text;

    private Integer itemId;

    private Integer userId;

    private LocalDateTime created;
}
