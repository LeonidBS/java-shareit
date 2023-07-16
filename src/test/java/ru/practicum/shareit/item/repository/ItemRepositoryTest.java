package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles(profiles = "test")
class ItemRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;
    private User owner;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;

    @BeforeEach
    private void setup() {
        requestor = InstanceFactory.newUser(null, "requestor", "requestor@user.com");
        owner = InstanceFactory.newUser(null, "owner", "owner@user.com");
        itemRequest = InstanceFactory.newItemRequest(null, "request", LocalDateTime.now(), requestor);
        item1 = InstanceFactory.newItem(null, "item1", "good item1",
                true, owner, itemRequest);
        item2 = InstanceFactory.newItem(null, "item2", "good item2",
                true, owner, null);
    }

    @Test
    void findBySearchTextWhenTextInAllItemsThenReturnAllItems() {
        List<Item> sourceItems = List.of(item1, item2);
        userRepository.save(requestor);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);
        Pageable pageable1 = PageRequest.of(0, 5);

        List<Item> targetItems = itemRepository.findBySearchText("item", pageable1).toList();

        assertEquals(sourceItems, targetItems);
    }

    @Test
    void findBySearchTextWhenTextInOneItemsThenReturnOneItems() {
        List<Item> sourceItems = List.of(item1);
        userRepository.save(requestor);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);
        Pageable pageable1 = PageRequest.of(0, 5);

        List<Item> targetItems = itemRepository.findBySearchText("item1", pageable1).toList();

        assertEquals(sourceItems, targetItems);
    }

    @Test
    void updateItemsAsIsNotAvailableByUserId() {
        userRepository.save(requestor);
        userRepository.save(owner);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);

        itemRepository.updateItemsAsIsNotAvailableByUserId(owner.getId());
        Item updatedItem1 = itemRepository.findByIdFetch(item1.getId());
        Item updatedItem2 = itemRepository.findByIdFetch(item2.getId());

        assertFalse(updatedItem1.getAvailable());
        assertNull(updatedItem1.getOwner());
        assertFalse(updatedItem2.getAvailable());
        assertNull(updatedItem2.getOwner());
    }
}