package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
                            ItemRepository itemRepository, @Qualifier("dbService") UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }


    @Override
    public List<BookingDto> findAllByBookerIdAndStatus(Integer bookerId, SearchBookingStatus state,
                                                       Integer from, Integer size) {

        return null;
    }

    @Override
    public List<BookingDto> findAllByOwnerIdAndStatus(Integer ownerId, SearchBookingStatus state, Integer from, Integer size) {
        return null;
    }

    @Override
    public List<BookingDto> findByStatusAndItemId(Integer itemId) {
        return null;
    }

    @Override
    public BookingDto findById(Integer bookingId, Integer userId) {
        return null;
    }

    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Integer bookerId) {
        User booker = UserMapper.mapToUser(userService.findById(bookerId));
        Optional<Item> optionalItem = itemRepository.findById(bookingDto.getItemId());

        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} is not exist", bookingDto.getItemId());
            throw new MyValidationException("There is not Item with ID " + bookingDto.getItemId());
        }

        Item item = optionalItem.get();

        if (!item.getAvailable()) {
            log.error("Item with ID {} is available", bookingDto.getItemId());
            throw new MyValidationException("Item (ID " + bookingDto.getItemId() + " is not available");
        }

        @Valid Booking booking = Booking.builder()
                .startDate(bookingDto.getStartDate())
                .endDate(bookingDto.getEndDate())
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
    public BookingDto updateApproving(Integer bookingId, Boolean approved, Integer ownerId) {
        return null;
    }

    @Override
    @Transactional
    public BookingDto update(BookingDto bookingDto) {
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {

    }
}
