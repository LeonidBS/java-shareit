package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestStorage;

    @Autowired
    public ItemRequestServiceImpl(@Qualifier("inMemory") ItemRequestRepository itemRequestStorage) {
        this.itemRequestStorage = itemRequestStorage;
    }

    public List<ItemRequestDto> findAll() {
        return ItemRequestMapper.listToItemRequestDto(itemRequestStorage.findAll());
    }

    @Override
    public ItemRequestDto findById(Integer id) {
        ItemRequest itemRequest = itemRequestStorage.findById(id);

        if (itemRequest == null) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        log.debug("itemRequest has been created: {}", itemRequest);

        return itemRequestStorage.create(itemRequest);
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        itemRequestStorage.update(itemRequest);
        log.debug("ItemRequest has been updated: {}", itemRequest);

        return itemRequest;
    }

    @Override
    public ItemRequest delete(Integer id) {

        return itemRequestStorage.delete(id);
    }

}
