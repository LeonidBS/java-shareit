package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDtoForItem {

    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private Integer bookerId;

}
