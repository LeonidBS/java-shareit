package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles(profiles = "test")
class BookingRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking presentBooking;
    private Item itemWithRequest;
    PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    void startUp() {
       owner = InstanceFactory.newUser(null, "owner",
                "owner@user.com");
        booker = InstanceFactory.newUser(null, "booker",
                "booker@user.com");
        userRepository.save(owner);
        userRepository.save(booker);

        ItemRequest itemRequest = InstanceFactory.newItemRequest(null, "request",
                LocalDateTime.now(), booker);
        itemRequestRepository.save(itemRequest);
        itemWithRequest = InstanceFactory.newItem(null, "itemWithRequest",
                "good itemWithRequest", true, owner, itemRequest);
        itemRepository.save(itemWithRequest);

        LocalDateTime pastStartDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime pastEndDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureStartDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime currentStartDateTime = LocalDateTime.parse(LocalDateTime.now().minusWeeks(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime currentEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusWeeks(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        pastBooking = InstanceFactory.newBooking(null, pastStartDateTime,
                pastEndDateTime, itemWithRequest, booker, BookingStatus.APPROVED);
        futureBooking = InstanceFactory.newBooking(null, futureStartDateTime,
                futureEndDateTime, itemWithRequest, booker, BookingStatus.WAITING);
        presentBooking = InstanceFactory.newBooking(null, currentStartDateTime,
                currentEndDateTime, itemWithRequest, booker, BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
        bookingRepository.save(presentBooking);

    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByBookerIdOrderByStartDesc(
                        booker.getId(), page).toList();

        assertEquals(3, targetList.size());
        assertEquals(futureBooking, targetList.get(0));
        assertEquals(presentBooking, targetList.get(1));
        assertEquals(pastBooking, targetList.get(2));
    }

    @Test
    void findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc() {
        List<Booking> targetList = bookingRepository
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                        booker.getId(), LocalDateTime.now(),
                        LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(presentBooking, targetList.get(0));
    }

    @Test
    void findByBookerIdAndEndLessThanOrderByEndDesc() {
        List<Booking> targetList = bookingRepository
                .findByBookerIdAndEndLessThanOrderByEndDesc(
                        booker.getId(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(pastBooking, targetList.get(0));
    }

    @Test
    void findByBookerIdAndStartGreaterThanOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByBookerIdAndStartGreaterThanOrderByStartDesc(
                        booker.getId(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(futureBooking, targetList.get(0));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(
                        booker.getId(), BookingStatus.WAITING, page).toList();

        assertEquals(1, targetList.size());
        assertEquals(futureBooking, targetList.get(0));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdOrderByStartDesc(
                        owner.getId(), page).toList();

        assertEquals(3, targetList.size());
        assertEquals(futureBooking, targetList.get(0));
        assertEquals(presentBooking, targetList.get(1));
        assertEquals(pastBooking, targetList.get(2));
    }

    @Test
    void findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc() {
        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(presentBooking, targetList.get(0));
    }

    @Test
    void findByItemOwnerIdAndEndLessThanOrderByEndDesc() {
        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdAndEndLessThanOrderByEndDesc(
                        owner.getId(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(pastBooking, targetList.get(0));
    }

    @Test
    void findByItemOwnerIdAndStartGreaterThanOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(
                        owner.getId(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(futureBooking, targetList.get(0));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        owner.getId(), BookingStatus.APPROVED, page).toList();

        assertEquals(2, targetList.size());
        assertEquals(presentBooking, targetList.get(0));
        assertEquals(pastBooking, targetList.get(1));
    }

    @Test
    void countByStatusAndItemId() {
        int itemId = itemWithRequest.getId();
        Integer targetCount = bookingRepository
                .countByStatusAndItemId(
                        BookingStatus.APPROVED, itemId);

        assertEquals(2, targetCount);
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartLessThanOrderByStartDesc() {
        Booking targetBooking = bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartLessThanOrderByStartDesc(
                        itemWithRequest.getId(), BookingStatus.APPROVED,
                        LocalDateTime.now());

        assertEquals(presentBooking, targetBooking);
    }

    @Test
    void findFirstBookingByItemIdAndStatusAndStartGreaterThanOrderByStart() {
        Booking targetBooking = bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartGreaterThanOrderByStart(
                        itemWithRequest.getId(), BookingStatus.WAITING,
                        LocalDateTime.now());

        assertEquals(futureBooking, targetBooking);
    }

    @Test
    void countByBookerIdAndItemIdAndStatusAndEndLessThan() {
        Integer targetCount = bookingRepository
                .countByBookerIdAndItemIdAndStatusAndEndLessThan(
                        booker.getId(), itemWithRequest.getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertEquals(1, targetCount);
    }

    @Test
    void updateBookingsDeletingByUserId() {
        bookingRepository.updateBookingsDeletingByUserId(
                booker.getId());
        List<Booking> targetListBooking = bookingRepository.findAll();

        assertNull(targetListBooking.get(0).getBooker());
        assertNull(targetListBooking.get(1).getBooker());
        assertNull(targetListBooking.get(2).getBooker());
    }
}