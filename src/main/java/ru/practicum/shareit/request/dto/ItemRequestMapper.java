package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        if (itemRequest != null) {
            return new ItemRequestDto(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    itemRequest.getCreated(),
                    itemRequest.getRequestor().getId(),
                    itemRequest.getRequestor().getName(),
                    itemRequest.getItems() != null ?
                    itemMapper.mapListToItemDto(itemRequest.getItems()) : null
            );
        } else {
            return null;
        }
    }
}


