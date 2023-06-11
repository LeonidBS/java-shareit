package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@Builder
public class Booking {

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
