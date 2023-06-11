package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartBookingDate(),
                booking.getEndBookingDate(),
                booking.getDescription(),
                booking.getItemId(),
                booking.getBookerId(),
                booking.getStatus()
        );
    }

    public static List<BookingDto> listToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(toBookingDto(booking));
        }
        return bookingsDto;
    }
}