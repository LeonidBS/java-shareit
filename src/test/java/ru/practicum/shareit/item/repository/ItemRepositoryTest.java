package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles(profiles = "test")
class ItemRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;


    @BeforeEach
    private void setup() {

    }

    @Test
    void findByIdFetch() {

    }

    @Test
    void findBySearchText() {
    }

    @Test
    void updateItemsAsIsNotAvailableByUserId() {
    }

    @Test
    void findByItemRequestId() {
    }

    @AfterEach
    private void cleanDatabase() {

    }
}