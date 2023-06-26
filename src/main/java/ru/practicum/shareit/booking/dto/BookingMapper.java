package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getItem().getOwner().getName(),
                booking.getBooker().getName(),
                booking.getBooker().getEmail(),
                booking.getStatus()
        );
    }

    public static List<BookingDto> listToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(mapToBookingDto(booking));
        }
        return bookingsDto;
    }
}