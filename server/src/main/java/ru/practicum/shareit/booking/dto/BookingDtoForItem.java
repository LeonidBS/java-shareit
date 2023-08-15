package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItem {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    private BookingStatus status;

    private Integer bookerId;

    @Override
    public String toString() {
        return "{id=" + id
                + ", start=" + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                + ", end=" + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                + ", status=" + status
                + ", bookerId=" + bookerId + "}";
    }

}
