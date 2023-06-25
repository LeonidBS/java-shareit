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
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;

    @Autowired
    public BookingServiceImpl(@Qualifier("inMemory") BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    @Override
    public List<BookingDto> findAll() {
        return BookingMapper.listToBookingDto(bookingStorage.findAll());
    }

    @Override
    public BookingDto findById(Integer id) {
        Booking booking = bookingStorage.findById(id);

        if (booking == null) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Booking create(Booking booking) {

        return bookingStorage.create(booking);
    }

    @Override
    public Booking update(Booking booking) {

        if (bookingStorage.findById(booking.getId()) == null) {
            log.error("Booking with ID {} has not been found", booking.getId());
            throw new IdNotFoundException("There is no User with ID: " + booking.getId());
        }

        bookingStorage.update(booking);
        log.debug("Booking has been updated: {}", booking);

        return booking;
    }

    @Override
    public Booking delete(Integer id) {

        return bookingStorage.delete(id);
    }

    @Override
    public List<Booking> findApprovedBookingByItemId(Integer itemId) {
        return bookingStorage.findApprovedBookingByItemId(itemId);
    }
}
