package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.MyValidationException;

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
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.findAll();
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable String id) {
        try {
            return bookingService.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
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
    public Booking delete(@PathVariable String id) {
        try {
            return bookingService.delete(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
    }
}
