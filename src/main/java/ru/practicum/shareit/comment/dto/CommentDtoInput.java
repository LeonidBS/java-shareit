package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDtoInput {

    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Parameter name is empty")
    @Size(min = 1, message = "Comments is empty")
    private String text;

    private Integer itemId;

    private Integer userId;

    @FutureOrPresent
    private LocalDateTime created;
}