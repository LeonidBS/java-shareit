package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingStorage {

    List<Booking> findAll();

    Booking findById(Integer id);

    Booking create(Booking booking);

    Booking update(Booking booking);

    Booking delete(Integer id);

    List<Booking> findApprovedBookingByItemId(Integer itemId);
}
