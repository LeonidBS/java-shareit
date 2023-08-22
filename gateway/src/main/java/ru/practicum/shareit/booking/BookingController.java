package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.StatusValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllByBookerIdAndStatus(@RequestHeader(USER_ID) Integer userId,
                                                            @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                            @Validated @RequestParam(name = "from", defaultValue = "0")
                                                            @PositiveOrZero(message
                                                                    = "page should be positive or 0") Integer from,
                                                            @Validated @RequestParam(name = "size", defaultValue = "20")
                                                            @Positive(message
                                                                    = "size should be positive number") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusValidationException("Unknown state: " + stateParam));
        log.info("Get Booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getAllByBookerIdAndStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerIdAndStatus(@RequestHeader(USER_ID) Integer ownerId,
                                                           @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                           @RequestParam(name = "from", defaultValue = "0")
                                                           @PositiveOrZero(message
                                                                   = "page should be positive or 0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "20")
                                                           @Positive(message
                                                                   = "size should be positive number") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusValidationException("Unknown state: " + stateParam));
        log.info("Get Booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);

        return bookingClient.getAllByOwnerIdAndStatus(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) Integer userId,
                                             @PathVariable Long bookingId) {
        log.info("Get Booking {}, userId={}", bookingId, userId);

        return bookingClient.getById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID) @Positive(message
            = "size should be positive number") Integer userId,
                                                @RequestBody @Validated BookingDtoInput bookingDtoInputDto) {
        log.info("Creating Booking {}, userId={}", bookingDtoInputDto, userId);

        return bookingClient.createBooking(userId, bookingDtoInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBookingStatus(@PathVariable @Positive(message
            = "size should be positive number") Integer bookingId,
                                                     @RequestParam(name = "approved") Boolean approved,
                                                     @RequestHeader(USER_ID) Integer ownerId) {
        log.info("Approving {} Booking with ID={} and ownerId={}", approved, bookingId, ownerId);

        return bookingClient.patchBookingStatus(ownerId, bookingId, approved);
    }

    @PutMapping
    public ResponseEntity<Object> updateBooking(@RequestBody @Validated BookingDtoInput bookingDtoInput) {
        log.info("Updating Booking {}", bookingDtoInput);

        return bookingClient.updateBooking(bookingDtoInput);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBooking(@PathVariable Integer id) {
        log.info("Delete Booking with ID={}", id);

        return bookingClient.deleteBooking(id);
    }

}
