package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ApprovingException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingDbService implements BookingService {
    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    @Qualifier("dbService")
    private final UserService userService;

    @Override
    public List<BookingDto> findAllByBookerIdAndStatus(Integer bookerId, SearchBookingStatus state,
                                                       Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(bookerId);

        switch (state) {
            case ALL:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdOrderByStartDesc(bookerId, page).toList());
            case CURRENT:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                                bookerId, LocalDateTime.now(),
                                LocalDateTime.now(), page).toList());
            case PAST:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdAndEndLessThanOrderByEndDesc(bookerId, LocalDateTime.now(),
                                page).toList());
            case FUTURE:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdAndStartGreaterThanOrderByStartDesc(bookerId, LocalDateTime.now(),
                                page).toList());
            case WAITING:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, page).toList());
            case REJECTED:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, page).toList());
            default:
                return null;
        }
    }

    @Override
    public List<BookingDto> findAllByOwnerIdAndStatus(Integer ownerId,
                                                      SearchBookingStatus state,
                                                      Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(ownerId);

        switch (state) {
            case ALL:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdOrderByStartDesc(ownerId, page).toList());
            case CURRENT:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                                ownerId, LocalDateTime.now(),
                                LocalDateTime.now(), page).toList());
            case PAST:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdAndEndLessThanOrderByEndDesc(ownerId, LocalDateTime.now(),
                                page).toList());
            case FUTURE:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(ownerId, LocalDateTime.now(),
                                page).toList());
            case WAITING:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, page).toList());
            case REJECTED:
                return BookingMapper.mapListToBookingDto(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, page).toList());
            default:
                return null;
        }
    }

    @Override
    public BookingDto findByIdWithValidation(Integer bookingId, Integer userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking with ID {} has not been found", bookingId);
                    return new IdNotFoundException("There is no Booking with ID: " + bookingId);
                });

        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            log.error("Access denied for userId {}", userId);
            throw new AccessDeniedException("Access denied for userId " + userId);
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto create(BookingDtoInput bookingDtoInput, Integer bookerId) {
        User booker = UserMapper.mapToUser(userService.findById(bookerId));

        Item item = itemRepository.findById(bookingDtoInput.getItemId())
                .orElseThrow(() -> {
                    log.error("Item with ID {} is not exist", bookingDtoInput.getItemId());
                    return new IdNotFoundException("There is not Item with ID " + bookingDtoInput.getItemId());
                });

        if (!item.getAvailable()) {
            log.error("Item with ID {} is not available", bookingDtoInput.getItemId());
            throw new MyValidationException("Item (ID " + bookingDtoInput.getItemId() + " is not available");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            log.error("Item with ID {} belongs to Booker ", bookingDtoInput.getItemId());
            throw new AccessDeniedException("Item (ID " + bookingDtoInput.getItemId() + " belongs to Booker");
        }

        if (!bookingDtoInput.getEnd().isAfter(bookingDtoInput.getStart())) {
            log.error("Start must be before End");
            throw new MyValidationException("Start must be before End");
        }

        @Valid Booking booking = Booking.builder()
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();


        log.debug("Booking has been created: {}", booking);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Integer bookingId, Boolean approved, Integer ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking with ID {} has not been found", bookingId);
                    return new IdNotFoundException("There is no Booking with ID: " + bookingId);
                });

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.error("Access denied for ownerId {}", ownerId);
            throw new AccessDeniedException("Access denied for ownerId " + ownerId);
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Booking has been approved already");
            throw new MyValidationException("Booking has been approved already");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.error("Booking has not been requested");
            throw new ApprovingException("Booking has not been requested");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Booking {} has been approved", booking);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Booking {} has been rejected", booking);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(BookingDtoInput bookingDtoInput) {
        Booking booking = bookingRepository.findById(bookingDtoInput.getId())
                .orElseThrow(() -> {
                    log.error("Booking with ID {} has not been found", bookingDtoInput);
                    return new IdNotFoundException("There is no Booking with ID: " + bookingDtoInput);
                });

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Integer quantityBookingByStatusAndItemId(BookingStatus status, Integer itemId) {

        return bookingRepository.countByStatusAndItemId(status, itemId);
    }

    @Override
    public BookingDtoForItem findLastBookingByItemId(Integer itemId) {

        return BookingMapperForItem.INSTANCE.mapToDto(bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartLessThanOrderByStartDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()));
    }

    @Override
    public BookingDtoForItem findNextBookingByItemId(Integer itemId) {

        return BookingMapperForItem.INSTANCE.mapToDto(bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartGreaterThanOrderByStart(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()));
    }
}
