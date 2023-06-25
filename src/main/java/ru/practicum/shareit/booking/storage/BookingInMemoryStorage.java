package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class BookingInMemoryStorage implements BookingStorage {
    private final Map<Integer, Booking> bookings = new HashMap<>();
    private int id = 1;

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public Booking findById(Integer id) {

        return bookings.get(id);
    }

    @Override
    public Booking create(Booking booking) {
        booking.setId(id++);
        bookings.put(booking.getId(), booking);

        return booking;
    }

    @Override
    public Booking update(Booking booking) {

        bookings.put(booking.getId(), booking);

        return booking;
    }

    @Override
    public Booking delete(Integer id) {
        Booking booking = bookings.get(id);
        bookings.remove(id);

        return booking;
    }

    @Override
    public List<Booking> findApprovedBookingByItemId(Integer itemId) {
        List<Booking> bookingsByItem = new ArrayList<>();

        for (Booking booking : bookings.values()) {
            if (booking.getItemId().equals(itemId) && booking.getStatus() == BookingStatus.APPROVED) {
                bookingsByItem.add(booking);
            }
        }

        return bookingsByItem;
    }
}
