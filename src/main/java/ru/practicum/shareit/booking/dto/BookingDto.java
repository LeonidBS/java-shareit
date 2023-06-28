package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    public BookingDto(Integer id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
    }

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

    private UserDto booker;

    private ItemDtoForBooking item;
}
