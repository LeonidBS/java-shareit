package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoInput {
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Parameter name is empty")
    @Size(min = 1, message = "Comments is empty")
    private String text;

    private Integer itemId;

    private Integer userId;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime created;
}
