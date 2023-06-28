package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapperForItem {
    public static BookingDtoForItem mapToBookingDto(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker().getId()
       );
    }

    public static List<BookingDtoForItem> mapListToBookingDto(List<Booking> bookings) {
        List<BookingDtoForItem> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(mapToBookingDto(booking));
        }
        return bookingsDto;
    }
}