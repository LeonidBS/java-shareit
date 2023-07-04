package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestDate(),
                itemRequest.getRequestor().getId(),
                itemRequest.getRequestor().getName(),
                new ArrayList<>()
        );
    }

    public static List<ItemRequestDto> mapListToDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(mapToDto(itemRequest));
        }
        return itemRequestsDto;
    }
}