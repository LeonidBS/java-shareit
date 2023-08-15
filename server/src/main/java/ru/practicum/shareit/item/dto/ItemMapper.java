package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingDbService bookingService;

    public ItemDto mapToItemDto(Item item) {

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getOwner().getName(),
                item.getItemRequest() != null ?
                        item.getItemRequest().getId() : null,
                bookingService.quantityBookingByStatusAndItemId(BookingStatus.APPROVED, item.getId())
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
