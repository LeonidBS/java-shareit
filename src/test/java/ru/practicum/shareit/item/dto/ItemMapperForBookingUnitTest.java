package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperForBookingUnitTest {
    @Test
    void mapToItemDtoWhenOwnerNotNullItemRequestNotNull() {
        User requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        User owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        ItemRequest itemRequest = InstanceFactory.newItemRequest(1, "request",
                LocalDateTime.now(), requestor);
        Item item = InstanceFactory.newItem(1, "item", "good item",
                true, owner, itemRequest);
        ItemDtoForBooking itemDto = InstanceFactory.newItemDtoForBooking(1, "item", "good item",
                true, 2, 1);

        ItemDtoForBooking targetDto = ItemMapperForBooking.mapToItemDto(item);

        assertEquals(itemDto, targetDto);
    }
}