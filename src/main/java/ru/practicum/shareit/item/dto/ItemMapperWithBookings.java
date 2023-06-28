package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperWithBookings {
    private final BookingDbService bookingService;

    public ItemDtoWithBookings mapToItemDto(Item item, Integer userId) {

        return new ItemDtoWithBookings(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ?
                        item.getOwner().getId().equals(userId) ?
                                bookingService.findLastBookingByItemId(item.getId()) : null
                        : null,
                item.getOwner() != null ?
                        item.getOwner().getId().equals(userId) ?
                                bookingService.findNextBookingByItemId(item.getId()) : null
                        : null,
                item.getOwner().getId(),
                item.getOwner().getName(),
                item.getItemRequest() != null ?
                        item.getItemRequest().getRequestDate() : null,
                bookingService.quantityBookingByStatusAndItemId(BookingStatus.APPROVED, item.getId())
        );
    }

    public Item mapToItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public List<ItemDtoWithBookings> mapListToItemDto(List<Item> items, Integer userId) {
        List<ItemDtoWithBookings> itemDtoWithBookings = new ArrayList<>();
        for (Item item : items) {
            itemDtoWithBookings.add(mapToItemDto(item, userId));
        }
        return itemDtoWithBookings;
    }
}

