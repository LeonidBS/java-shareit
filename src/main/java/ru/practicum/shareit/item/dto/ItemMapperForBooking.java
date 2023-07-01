package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapperForBooking {

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
}
