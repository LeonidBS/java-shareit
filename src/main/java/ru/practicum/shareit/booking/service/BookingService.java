package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.SearchBookingStatus;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    List<BookingDto> findAllByBookerIdAndStatus(Integer bookerId, SearchBookingStatus state, Integer from, Integer size);

    List<BookingDto> findAllByOwnerIdAndStatus(Integer ownerId, SearchBookingStatus state, Integer from, Integer size);

    List<BookingDto> findByStatusAndItemId(Integer itemId);
    BookingDto findById(Integer bookingId, Integer userId);

    BookingDto create(BookingDto bookingDto, Integer bookerId);

    BookingDto updateApproving(Integer bookingId, Boolean approved, Integer ownerId);

    BookingDto update(BookingDto bookingDto);

    void delete(Integer id);
}
