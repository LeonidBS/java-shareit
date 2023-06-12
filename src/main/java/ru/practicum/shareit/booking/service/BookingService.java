package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    List<BookingDto> findAll();

    BookingDto findById(Integer id);

    Booking create(Booking booking);

    Booking update(Booking booking);

    Booking delete(Integer id);

    List<Booking> findApprovedBookingByItemId(Integer itemId);
}
