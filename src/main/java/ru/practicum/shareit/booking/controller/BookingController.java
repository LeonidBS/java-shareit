package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingDbService bookingService;

    @GetMapping
    public List<BookingDto> findAllByBookerIdAndStatus(@RequestHeader("X-Sharer-User-Id") Integer bookerId,
                                                       @RequestParam(defaultValue = "ALL") SearchBookingStatus state,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "20")  Integer size) {

        return bookingService.findAllByBookerIdAndStatus(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerIdAndStatus(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                      @RequestParam(defaultValue = "ALL") SearchBookingStatus state,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "20")  Integer size) {

        return bookingService.findAllByOwnerIdAndStatus(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Integer bookingId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.findByIdWithValidation(bookingId, userId);
    }

    @PostMapping
    public BookingDto create(@RequestBody @Validated(ValidationGroups.Create.class) BookingDtoInput bookingDtoInput,
                             @RequestHeader("X-Sharer-User-Id") Integer bookerId) {

        return bookingService.create(bookingDtoInput, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@PathVariable Integer bookingId, @RequestParam Boolean approved,
                            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return bookingService.patchBooking(bookingId, approved, ownerId);
    }

    @PutMapping
    public BookingDto update(@RequestBody @Validated(ValidationGroups.Create.class) BookingDtoInput bookingDtoInput) {

        return bookingService.update(bookingDtoInput);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        bookingService.delete(id);
    }
}
