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
public class ItemMapper {
    private final BookingDbService bookingService;
    private final ItemRequestService itemRequestService;

    public ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ?
                        item.getItemRequest().getRequestDate() : null,
                bookingService.findByStatusAndItemId(item.getId()).size()
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

    public List<ItemDto> mapListToItemDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(mapToItemDto(item));
        }
        return itemsDto;
    }
}
