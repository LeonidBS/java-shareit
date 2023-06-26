package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto MapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getName(),
                itemRequest.getRequestDate()
        );
    }

    public static ItemRequest MapToItemRequest(ItemRequestDto itemRequest, User requestor) {
        return new ItemRequest(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requestor,
                itemRequest.getRequestDate()
        );
    }

    public static List<ItemRequestDto> listToItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsDto.add(MapToItemRequestDto(itemRequest));
        }
        return itemRequestsDto;
    }
}