package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@Builder
public class BookingDto {

    @PositiveOrZero
    private Integer id;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer itemId;

    private String itemName;

    private String itemOwnerName;

    private String bookerName;

    private String bookerEmail;

    private BookingStatus status;
}
