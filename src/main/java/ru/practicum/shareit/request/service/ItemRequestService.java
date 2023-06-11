package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import java.util.List;

@Slf4j
@Service
public class ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;

    @Autowired
    public ItemRequestService(@Qualifier("inMemory") ItemRequestStorage itemRequestStorage) {
        this.itemRequestStorage = itemRequestStorage;
    }

    public List<ItemRequestDto> findAll() {
        return ItemRequestMapper.listToItemRequestDto(itemRequestStorage.findAll());
    }

    public ItemRequestDto findById(Integer id) {
        ItemRequest itemRequest = itemRequestStorage.findById(id);

        if (itemRequest == null) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public ItemRequest create(ItemRequest itemRequest) {
        log.debug("itemRequest has been created: {}", itemRequest);

        return itemRequestStorage.create(itemRequest);
    }

    public ItemRequest update(ItemRequest itemRequest) {
        itemRequestStorage.update(itemRequest);
        log.debug("ItemRequest has been updated: {}", itemRequest);

        return itemRequest;
    }

    public ItemRequest delete(Integer id) {

        return itemRequestStorage.delete(id);
    }

}
