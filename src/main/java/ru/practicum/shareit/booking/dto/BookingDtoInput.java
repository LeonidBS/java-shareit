package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDtoInput {

    @PositiveOrZero
    private Integer id;

    @FutureOrPresent
    @NotNull(message = "Parameter startDate is NULL")
    //   @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime start;

    @FutureOrPresent
    @NotNull(message = "Parameter endDate is NULL")
    // @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime end;

    private BookingStatus status;

    private Integer bookerId;

    private Integer ItemId;
}
