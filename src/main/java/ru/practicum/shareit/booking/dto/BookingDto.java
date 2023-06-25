package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startBookingDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endBookingDate;

    @Size(max = 200, message = "Длина описания превышает максимально допустиму 200 символов")
    private String description;

    @PositiveOrZero
    private Integer itemId;

    @PositiveOrZero
    private Integer bookerId;

    private BookingStatus status;

}
