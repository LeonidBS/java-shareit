package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private Integer id;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime start;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime end;

    private BookingStatus status;

    private UserDto booker;

    private ItemDtoForBooking item;
}
