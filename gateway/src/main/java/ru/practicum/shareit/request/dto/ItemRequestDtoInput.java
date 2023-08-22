package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDtoInput {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @NotNull(message = "Parameter name is NULL")
    @Size(min = 1, message = "Request description is empty")
    private String description;

    @FutureOrPresent
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime requestDate;
}
