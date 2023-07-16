package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles(profiles = "test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clean.sql")
class UserDbServiceIntegrationTest {
    private final EntityManager em;
    private final @Qualifier("dbService") UserService userService;

    @Test
    void findAllReturnUsers() {

        List<UserDto> sourceUsers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            sourceUsers.add(InstanceFactory.newUserDto("user" + i,
                    "user" + i + "@user.com"));
        }

        for (UserDto entity : sourceUsers) {
            em.persist(UserMapper.mapToUser(entity));
        }
        em.flush();
        List<UserDto> targetUsers = userService.findAll(0, 10);
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        int id = 0;
        for (UserDto userDto : sourceUsers) {
            id++;
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", equalTo(id)),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void deleteByIdWhen() {
        User sourceUser = InstanceFactory.newUser(null, "user", "user@user.com");
        em.persist(sourceUser);
        ItemRequest itemRequest = InstanceFactory.newItemRequest(null,
                "request description", LocalDateTime.now().plusMinutes(1), sourceUser);
        em.persist(itemRequest);
        Item item = InstanceFactory.newItem(null, "item", "description",
                true, sourceUser, null);
        em.persist(item);
        Booking booking = InstanceFactory.newBooking(null, LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1), item, sourceUser, BookingStatus.WAITING);
        em.persist(booking);
        Comment comment = InstanceFactory.newComment(null, "comment text1", item, sourceUser,
                LocalDateTime.now().plusMinutes(2));
        em.persist(comment);
        em.flush();

        userService.deleteById(sourceUser.getId());

        TypedQuery<User> queryUser = em.createQuery("SELECT u FROM User u", User.class);
        List<User> allUsers = queryUser.getResultList();
        TypedQuery<ItemRequest> queryRequest = em.createQuery("SELECT ir FROM ItemRequest ir", ItemRequest.class);
        ItemRequest targetRequest = queryRequest.getResultList().get(0);
        TypedQuery<Item> queryItem = em.createQuery("SELECT i FROM Item i", Item.class);
        Item targetItem = queryItem.getResultList().get(0);
        TypedQuery<Booking> queryBooking =  em.createQuery("SELECT b FROM Booking b", Booking.class);
        Booking targetBooking = queryBooking.getResultList().get(0);
        TypedQuery<Comment> queryComment = em.createQuery("SELECT c FROM Comment c", Comment.class);
        List<Comment> allComments = queryComment.getResultList();

        assertThat(allUsers, is(empty()));
        assertThat(targetRequest.getRequestor(), is(nullValue()));
        assertThat(targetItem.getAvailable(), equalTo(false));
        assertThat(targetItem.getOwner(), is(nullValue()));
        assertThat(targetBooking.getId(), equalTo(1));
        assertThat(targetBooking.getBooker(), is(nullValue()));
        assertThat(allComments, is(empty()));
    }

@AfterEach
    void tearDown() {
        em.clear();
}
}