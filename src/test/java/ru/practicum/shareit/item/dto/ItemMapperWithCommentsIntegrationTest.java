package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles(profiles = "test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
class ItemMapperWithCommentsIntegrationTest {

    private final EntityManager em;
    private final ItemMapperWithComments itemMapperWithComments;

    private User owner;
    private BookingDtoForItem lastBookingDto;
    private BookingDtoForItem nextBookingDto;
    private ItemRequest itemRequest;
    private Item item;
    private CommentDtoForItem commentDtoForItem;

    @BeforeEach
    void setup() {
        User requestor = InstanceFactory.newUser(null, "requestor", "requestor@user.com");
        owner = InstanceFactory.newUser(null, "owner", "owner@user.com");
        User author = InstanceFactory.newUser(null, "author", "author@user.com");
        em.persist(requestor);
        em.persist(owner);
        em.persist(author);
        itemRequest = InstanceFactory.newItemRequest(null, "request", LocalDateTime.now(), requestor);
        em.persist(itemRequest);
        item = InstanceFactory.newItem(null, "item", "good item",
                true, owner, itemRequest);
        em.persist(item);
        LocalDateTime lastBookingStartDateTime = LocalDateTime.now().minusMonths(2)
                .minusNanos(LocalDateTime.now().minusMonths(2).getNano());
        LocalDateTime lastBookingEndDateTime = LocalDateTime.now().minusMonths(1)
                .minusNanos(LocalDateTime.now().minusMonths(1).getNano());
        LocalDateTime nextBookingStartDateTime = LocalDateTime.now().plusMonths(1)
                .minusNanos(LocalDateTime.now().plusMonths(1).getNano());
        LocalDateTime nextBookingEndDateTime = LocalDateTime.now().plusMonths(2)
                .minusNanos(LocalDateTime.now().plusMonths(2).getNano());
        Booking lastBooking = InstanceFactory.newBooking(null, lastBookingStartDateTime,
                lastBookingEndDateTime, item, requestor, BookingStatus.APPROVED);
        Booking nextBooking = InstanceFactory.newBooking(null, nextBookingStartDateTime,
                nextBookingEndDateTime, item, requestor, BookingStatus.APPROVED);
        lastBookingDto = InstanceFactory.newBookingDtoForItem(1, lastBooking.getStart(),
                lastBooking.getEnd(), BookingStatus.APPROVED, requestor.getId());
        nextBookingDto = InstanceFactory.newBookingDtoForItem(2, nextBooking.getStart(),
                nextBooking.getEnd(), BookingStatus.APPROVED, requestor.getId());
        em.persist(lastBooking);
        em.persist(nextBooking);
        Comment comment = InstanceFactory.newComment(null, "comment", item,
                author, LocalDateTime.now());
        commentDtoForItem = InstanceFactory.newCommentDtoForItem(1, "comment",
                item.getId(), author.getId(), author.getName(), comment.getCreated());
        em.persist(comment);
        em.flush();
    }

    @Test
    void mapToItemDtoWhenUserIsOwnerRequestNotNull() {
        int userId = 2;

        ItemDtoWithComments itemDtoWithComments = InstanceFactory.newItemDtoWithComments(
                1, "item", "good item", true, lastBookingDto,
                nextBookingDto, List.of(commentDtoForItem), owner.getId(), owner.getName(),
                itemRequest.getCreated(), 2);

        ItemDtoWithComments targetDto = itemMapperWithComments.mapToItemDto(item, owner,
                itemRequest, userId);

        assertThat(targetDto, equalTo(itemDtoWithComments));
    }

    @Test
    void mapToItemDtoWhenUserIsNotOwnerRequestNotNull() {
        int userId = 3;

        ItemDtoWithComments itemDtoWithComments = InstanceFactory.newItemDtoWithComments(
                1, "item", "good item", true, null,
                null, List.of(commentDtoForItem), owner.getId(), owner.getName(),
                itemRequest.getCreated(), 2);

        ItemDtoWithComments targetDto = itemMapperWithComments.mapToItemDto(item, owner,
                itemRequest, userId);

        assertThat(targetDto, equalTo(itemDtoWithComments));
    }

}