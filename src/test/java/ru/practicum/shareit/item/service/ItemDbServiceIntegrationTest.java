package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles(profiles = "test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
class ItemDbServiceIntegrationTest {

    private final EntityManager em;
    private final ItemDbService itemDbService;

    @Test
    void createComment() {
        int userId = 3;
        int itemId = 1;
        User requestor = InstanceFactory.newUser(null, "requestor", "requestor@user.com");
        User owner = InstanceFactory.newUser(null, "owner", "owner@user.com");
        User author = InstanceFactory.newUser(null, "author", "author@user.com");
        em.persist(requestor);
        em.persist(owner);
        em.persist(author);
        ItemRequest itemRequest = InstanceFactory.newItemRequest(null, "request", LocalDateTime.now(), requestor);
        em.persist(itemRequest);
        Item item = InstanceFactory.newItem(null, "item", "good item",
                true, owner, itemRequest);
        em.persist(item);
        Booking booking = InstanceFactory.newBooking(null, LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusMonths(1), item, author, BookingStatus.APPROVED);
        em.persist(booking);

        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("comment")
                .created(LocalDateTime.now())
                .build();

        CommentDto targetDto = itemDbService.createComment(commentDtoInput, itemId, userId);

        assertThat(targetDto.getText(), equalTo(commentDtoInput.getText()));
        assertThat(targetDto.getItemId(), equalTo(itemId));
        assertThat(targetDto.getItemName(), equalTo(item.getName()));
        assertThat(targetDto.getAuthorId(), equalTo(userId));
        assertThat(targetDto.getAuthorName(), equalTo("author"));
        assertThat(targetDto.getCreated(), equalTo(commentDtoInput.getCreated()));
    }
}