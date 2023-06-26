package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingDbService bookingService;

    @GetMapping
    public List<BookingDto> findAllByBookerIdAndStatus(@RequestHeader("X-Sharer-User-Id") Integer bookerId,
                                                       @RequestParam(defaultValue = "ALL") SearchBookingStatus state) {
        int from = 0;
        int size = 10;

        return bookingService.findAllByBookerIdAndStatus(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerIdAndStatus(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                      @RequestParam(defaultValue = "ALL") SearchBookingStatus state) {
        int from = 0;
        int size = 10;

        return bookingService.findAllByOwnerIdAndStatus(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Integer bookingId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDto create(@RequestBody @Validated(ValidationGroups.Create.class) BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Integer bookerId) {

        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Integer bookingId, @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return bookingService.updateApproving(bookingId, approved, ownerId);
    }

    @PutMapping
    public BookingDto update(@RequestBody @Validated(ValidationGroups.Create.class) BookingDto bookingDto) {

        return bookingService.update(bookingDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        bookingService.delete(id);
    }
}
