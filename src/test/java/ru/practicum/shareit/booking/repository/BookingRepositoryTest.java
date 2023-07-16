package ru.practicum.shareit.booking.repository;

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

    @Test
    void findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc() {
        User owner = InstanceFactory.newUser(null, "owner",
                "owner@user.com");
        User booker = InstanceFactory.newUser(null, "booker",
                "booker@user.com");
        userRepository.save(owner);
        userRepository.save(booker);

        ItemRequest itemRequest = InstanceFactory.newItemRequest(null, "request",
                LocalDateTime.now(), booker);
        itemRequestRepository.save(itemRequest);
        Item itemWithRequest = InstanceFactory.newItem(null, "itemWithRequest",
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
        Booking pastBooking = InstanceFactory.newBooking(null, pastStartDateTime,
                pastEndDateTime, itemWithRequest, booker, BookingStatus.APPROVED);
        Booking futureBooking = InstanceFactory.newBooking(null, futureStartDateTime,
                futureEndDateTime, itemWithRequest, booker, BookingStatus.WAITING);
        Booking presentBooking = InstanceFactory.newBooking(null, currentStartDateTime,
                currentEndDateTime, itemWithRequest, booker, BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
        bookingRepository.save(presentBooking);
        PageRequest page = PageRequest.of(0, 10);

        List<Booking> targetList = bookingRepository
                .findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now(), page).toList();

        assertEquals(1, targetList.size());
        assertEquals(presentBooking, targetList.get(0));
    }
}