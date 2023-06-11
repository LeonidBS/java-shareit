package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.IdNotFoundException;

import java.util.List;

@Slf4j
@Service
public class BookingService {
    private final BookingStorage bookingStorage;

    @Autowired
    public BookingService(@Qualifier("inMemory") BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    public List<BookingDto> findAll() {
        return BookingMapper.listToBookingDto(bookingStorage.findAll());
    }

    public BookingDto findById(Integer id) {
        Booking booking = bookingStorage.findById(id);

        if (booking == null) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return BookingMapper.toBookingDto(booking);
    }

    public Booking create(Booking booking) {

        return bookingStorage.create(booking);
    }

    public Booking update(Booking booking) {

        if (bookingStorage.findById(booking.getId()) == null) {
            log.error("Booking with ID {} has not been found", booking.getId());
            throw new IdNotFoundException("There is no User with ID: " + booking.getId());
        }

        bookingStorage.update(booking);
        log.debug("Booking has been updated: {}", booking);

        return booking;
    }

    public Booking delete(Integer id) {

        return bookingStorage.delete(id);
    }

    public List<Booking> findApprovedBookingByItemId(Integer itemId) {
        return bookingStorage.findApprovedBookingByItemId(itemId);
    }
}
