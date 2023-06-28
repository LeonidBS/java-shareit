package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingDbService implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public BookingDbService(BookingRepository bookingRepository,
                            ItemRepository itemRepository,
                            @Qualifier("dbService") UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

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
                        .findCurrentByBookerId(bookerId, page).toList());
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
                        .findCurrentByOwnerId(ownerId, page).toList());
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
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            log.error("Booking with ID {} has not been found", bookingId);
            throw new IdNotFoundException("There is no Booking with ID: " + bookingId);
        }

        Booking booking = optionalBooking.get();

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
        Optional<Item> optionalItem = itemRepository.findById(bookingDtoInput.getItemId());


        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} is not exist", bookingDtoInput.getItemId());
            throw new IdNotFoundException("There is not Item with ID " + bookingDtoInput.getItemId());
        }

        Item item = optionalItem.get();

        if (!item.getAvailable()) {
            log.error("Item with ID {} is available", bookingDtoInput.getItemId());
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

        bookingRepository.save(booking);
        log.debug("Booking has been created: {}", booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Integer bookingId, Boolean approved, Integer ownerId) {
        Optional<Booking> optionalExistedBooking = bookingRepository.findById(bookingId);

        if (optionalExistedBooking.isEmpty()) {
            log.error("Booking with ID {} has not been found", bookingId);
            throw new IdNotFoundException("There is no Booking with ID: " + bookingId);
        }

        if (!optionalExistedBooking.get().getItem().getOwner().getId().equals(ownerId)) {
            log.error("Access denied for ownerId {}", ownerId);
            throw new AccessDeniedException("Access denied for ownerId " + ownerId);
        }

        if (optionalExistedBooking.get().getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Booking has been approved already");
            throw new MyValidationException("Booking has been approved already");
        }

        if (!optionalExistedBooking.get().getStatus().equals(BookingStatus.WAITING)) {
            log.error("Booking has not been requested");
            throw new ApprovingException("Booking has not been requested");
        }

        Booking booking = optionalExistedBooking.get();
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            booking.getItem().setAvailable(true);
            log.debug("Booking {} has been approved", optionalExistedBooking.get());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            booking.getItem().setAvailable(false);
            log.debug("Booking {} has been rejected", optionalExistedBooking.get());
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(BookingDtoInput bookingDtoInput) {
        Optional<Booking> optionalExistedBooking = bookingRepository.findById(bookingDtoInput.getId());

        if (optionalExistedBooking.isEmpty()) {
            log.error("Booking {} has not been found", bookingDtoInput);
            throw new IdNotFoundException("There is no Booking " + bookingDtoInput);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(optionalExistedBooking.get()));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Integer quantityBookingByStatusAndItemId(BookingStatus status, Integer itemId) {

        return bookingRepository.quantityBookingsByStatusAndItemId(status, itemId);
    }

    @Override
    public BookingDtoForItem findLastBookingByItemId(Integer itemId) {

        return bookingRepository
                .findFirstBookingByItemIdAndEndLessThanOrderByEndDesc(itemId,
                        LocalDateTime.now());
    }


    @Override
    public BookingDtoForItem findNextBookingByItemId(Integer itemId) {

        return bookingRepository
                .findFirstBookingByItemIdAndStartGreaterThanOrderByStart(itemId,
                        LocalDateTime.now());
    }
}
