package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findOwnerAll(Integer ownerId);

    ItemDto findById(Integer id);

    List<ItemDto> findBySearchText(String text);

    ItemDto create(ItemDto itemDto, Integer ownerId);

    ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId);
}
