package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperForBooking {
    private final BookingDbService bookingService;
    private final ItemRequestService itemRequestService;

    public static ItemDtoForBooking mapToItemDto(Item item) {
        return new ItemDtoForBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ?
                        item.getOwner().getId() : null,
                item.getItemRequest() != null ?
                        item.getItemRequest().getId() : null
        );
    }

    public static Item mapToItem(ItemDtoForBooking itemDtoForBooking, User user, ItemRequest itemRequest) {
        return new Item(
                itemDtoForBooking.getId(),
                itemDtoForBooking.getName(),
                itemDtoForBooking.getDescription(),
                itemDtoForBooking.getAvailable(),
                user,
                itemRequest
        );
    }

    public static List<ItemDtoForBooking> mapListToItemDto(List<Item> items) {
        List<ItemDtoForBooking> itemDtoForBookings = new ArrayList<>();
        for (Item item : items) {
            itemDtoForBookings.add(mapToItemDto(item));
        }
        return itemDtoForBookings;
    }
}
