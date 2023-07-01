package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapperForBooking;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker() != null ?
                        UserMapper.mapToUserDto(booking.getBooker()) : null,
                booking.getItem() != null ?
                        ItemMapperForBooking.mapToItemDto(booking.getItem()) : null
        );
    }

    public static List<BookingDto> mapListToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingDtoPosts = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoPosts.add(mapToBookingDto(booking));
        }
        return bookingDtoPosts;
    }
}