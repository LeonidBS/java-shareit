package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDto> findAllByOwnerId(Integer ownerId, int from, int size);

    ItemDto findById(Integer id);

    List<ItemDto> findBySearchText(String text, int from, int size);

    ItemDto create(ItemDto itemDto, Integer ownerId);

    ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId);
}
