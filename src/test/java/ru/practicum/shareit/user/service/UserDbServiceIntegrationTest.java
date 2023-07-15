package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
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
    void deleteByIdWhenUserExistThenDeleteUserAndUserIdInLinkedTablesUserItemsSetUnavailable() {
        UserDto sourceUser = InstanceFactory.newUserDto("user", "user@user.com");
        em.persist(UserMapper.mapToUser(sourceUser));

        TypedQuery<User> queryUser = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = queryUser.setParameter("id", 1)
                .getSingleResult();

        ItemRequest itemRequest = InstanceFactory.newItemRequest(null,
                "request description", LocalDateTime.now().plusMinutes(1), user);
        em.persist(itemRequest);

        Item item = InstanceFactory.newItem(null, "item", "description",
                true, user, null);
        em.persist(item);

        Booking booking = InstanceFactory.newBooking(null , LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1), item, user, BookingStatus.WAITING);
        em.persist(booking);

        Comment comment = InstanceFactory.newComment(null, "comment text", item, user,
                LocalDateTime.now().plusMinutes(2));
        em.persist(comment);
        em.flush();

        userService.deleteById(1);

        assertThat(userService.findAll(0, 10), hasSize(0));

        TypedQuery<Comment> queryComment = em.createQuery("SELECT c FROM Comment c", Comment.class);
        List<Comment> allComments = queryComment.getResultList();
        assertThat(allComments, is(empty()));

        em.refresh(itemRequest);
        assertThat(itemRequest.getId(), equalTo(1));
        assertThat(itemRequest.getRequestor(), is(nullValue()));

        em.refresh(item);
        assertThat(item.getId(), equalTo(1));
        assertThat(item.getAvailable(), equalTo(false));
        assertThat(item.getOwner(), is(nullValue()));

        em.refresh(booking);
        assertThat(booking.getId(), equalTo(1));
        assertThat(booking.getBooker(), is(nullValue()));
    }
}