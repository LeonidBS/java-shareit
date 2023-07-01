package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

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


}