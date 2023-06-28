package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchBookingStatus;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    List<BookingDto> findAllByBookerIdAndStatus(Integer bookerId, SearchBookingStatus state, Integer from, Integer size);

    List<BookingDto> findAllByOwnerIdAndStatus(Integer ownerId, SearchBookingStatus state, Integer from, Integer size);

    BookingDto findByIdWithValidation(Integer bookingId, Integer userId);

    BookingDtoForItem findLastBookingByItemId(Integer itemId);

    BookingDtoForItem findNextBookingByItemId(Integer itemId);

    BookingDto create(BookingDtoInput bookingDtoInput, Integer bookerId);

    BookingDto patchBooking(Integer bookingId, Boolean approved, Integer ownerId);

    BookingDto update(BookingDtoInput bookingDtoInput);

    void delete(Integer id);

    Integer quantityBookingByStatusAndItemId(BookingStatus status, Integer itemId);
}
