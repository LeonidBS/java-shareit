package ru.practicum.shareit.request.dto;


import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapperForCreate {
    public static ItemRequestDtoForCreate mapToDto(ItemRequest itemRequest) {
        return new ItemRequestDtoForCreate(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getRequestor().getId(),
                itemRequest.getRequestor().getName()
        );
    }

}