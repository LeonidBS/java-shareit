package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles(profiles = "test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void updateRequestsByDeletingUserId() {
        User requestor = InstanceFactory.newUser(null, "requestor",
                "requestor@user.com");
        userRepository.save(requestor);
        ItemRequest itemRequest = InstanceFactory.newItemRequest(null, "request",
                LocalDateTime.now(), requestor);
        itemRequestRepository.save(itemRequest);

        itemRequestRepository.updateRequestsByDeletingUserId(requestor.getId());
        ItemRequest targetItemRequest = itemRequestRepository.findById(itemRequest.getId()).get();

        assertEquals(targetItemRequest.getId(), itemRequest.getId());
        assertEquals(targetItemRequest.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequest.getRequestor(), requestor);
        assertNull(targetItemRequest.getRequestor());
    }

    @AfterEach
    void cleanDatabase() {
        itemRequestRepository.deleteAllItemRequest();
        userRepository.deleteAllUser();
    }
}