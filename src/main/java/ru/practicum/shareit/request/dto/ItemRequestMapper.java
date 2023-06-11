package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getRequestDate()
        );
    }

    public static List<ItemRequestDto> listToItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(toItemRequestDto(itemRequest));
        }
        return itemRequestsDto;
    }
}