package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getRequestor().getId(),
                itemRequest.getRequestor().getName(),
                new ArrayList<>()
        );
    }

}