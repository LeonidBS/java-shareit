package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> findOwn(Integer requestorId);

    List<ItemRequestDto> findAllExceptOwn(Integer requestorId, Integer from, Integer size);

    ItemRequestDto getById(Integer requestId, Integer userId);

    ItemRequestDto create(ItemRequestDtoInput dtoInput, Integer requestorId);

    void delete(Integer id);
}
