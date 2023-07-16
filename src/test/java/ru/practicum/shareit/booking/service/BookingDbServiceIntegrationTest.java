package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles(profiles = "test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
class BookingDbServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void patchBooking() {
        User booker = InstanceFactory.newUser(null, "booker", "booker@user.com");
        User owner = InstanceFactory.newUser(null, "owner", "owner@user.com");
        em.persist(booker);
        em.persist(owner);
        ItemRequest itemRequest = InstanceFactory.newItemRequest(null, "request",
                LocalDateTime.now(), booker);
        em.persist(itemRequest);
        Item itemWithRequest = InstanceFactory.newItem(null, "itemWithRequest",
                "good itemWithRequest", true, owner, itemRequest);
        em.persist(itemWithRequest);
        ItemDtoForBooking itemDtoForBookingWithRequest = InstanceFactory.newItemDtoForBooking(1,
                "itemWithRequest", "good itemWithRequest",
                true, 2, 1);
        LocalDateTime futureStartDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        Booking futureBooking = InstanceFactory.newBooking(null, futureStartDateTime,
                futureEndDateTime, itemWithRequest, booker, BookingStatus.WAITING);
        em.persist(futureBooking);

        int bookingId = 1;
        Boolean approved = true;
        int ownerId = 2;

        BookingDto targetDto = bookingService.patchBooking(bookingId, approved, ownerId);

        assertEquals(futureBooking.getStart(), targetDto.getStart());
        assertEquals(futureBooking.getEnd(), targetDto.getEnd());
        assertEquals(itemDtoForBookingWithRequest, targetDto.getItem());
        assertEquals(UserMapper.mapToUserDto(booker), targetDto.getBooker());
        assertEquals(BookingStatus.APPROVED, targetDto.getStatus());
    }
}