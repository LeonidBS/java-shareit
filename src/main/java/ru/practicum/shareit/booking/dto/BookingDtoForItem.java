package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDtoForItem {

    private Integer id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    private BookingStatus status;

    private Integer bookerId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BookingDtoForItem (@JsonProperty("id") Integer id,
               @JsonProperty("start") LocalDateTime start,
    @JsonProperty("end") LocalDateTime end,
    @JsonProperty("status") BookingStatus status,
    @JsonProperty("bookerId") Integer bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.bookerId = bookerId;
    }

    @Override
    public String toString() {
        return "{id=" + id
                + ", start=" + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                + ", end=" + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                + ", status=" + status
                + ", bookerId=" + bookerId + "}";
    }

}
