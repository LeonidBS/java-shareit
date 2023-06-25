package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceImpl bookingService;

    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.findAll();
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Integer id) {

        return bookingService.findById(id);
    }

    @PostMapping
    public Booking create(@Valid @RequestBody Booking booking) {

        return bookingService.create(booking);
    }

    @PutMapping
    public Booking update(@Valid @RequestBody Booking booking) {

        return bookingService.update(booking);
    }

    @DeleteMapping("/{id}")
    public Booking delete(@PathVariable Integer id) {

        return bookingService.delete(id);
    }
}
