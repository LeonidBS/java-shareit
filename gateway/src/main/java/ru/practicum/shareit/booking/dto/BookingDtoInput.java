package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoInput {
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Positive
    private Integer itemId;

    @FutureOrPresent(message = "Date should be in future or present")
    @NotNull(message = "Parameter name is empty")
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime start;

    @Future(message = "Date should be in future")
    @NotNull(message = "Parameter name is empty")
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime end;
}
