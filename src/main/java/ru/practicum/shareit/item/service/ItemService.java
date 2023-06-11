package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(@Qualifier("inMemory") ItemStorage itemStorage,
                       UserService userService,
                       @Qualifier("inMemory") ItemRequestStorage itemRequestStorage,
                       ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemRequestStorage = itemRequestStorage;
        this.itemMapper = itemMapper;
    }

    public List<ItemDto> findOwnerAll(Integer ownerId) {
        userService.findById(ownerId);

        return itemMapper.listToItemDto(itemStorage.findOwnerAll(ownerId));
    }

    public ItemDto findById(Integer id) {
        Item item = itemStorage.findById(id);

        if (item == null) {
            log.error("Item with ID {} has not been found", id);
            throw new IdNotFoundException("There is no Item with ID: " + id);
        }

        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> findBySearchText(String text) {
        if (text != null) {
            if (!text.isEmpty()) {
                return itemMapper.listToItemDto(itemStorage.findBySearchText(text));
            }
        }
        return new ArrayList<>();
    }

    public Item create(ItemDto itemDto, Integer ownerId) {
        userService.findById(ownerId);

        if (itemDto.getAvailable() == null) {
            log.error("Available is NULL");
            throw new MyValidationException("Available is NULL");
        }

        @Valid Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .itemRequest(itemRequestStorage.findById(itemDto.getItemRequestId()))
                .build();

        itemStorage.create(item);
        log.debug("ItemRequest has been created: {}", item);
        return item;
    }

    public Item update(ItemDto itemDto, Integer ownerId, Integer itemId) {
        userService.findById(ownerId);
        Item excistedItem = itemStorage.findById(itemId);

        if (excistedItem == null) {
            log.error("User with ID {} has not been found", itemId);
            throw new IdNotFoundException("There is no User with ID: " + itemId);
        }

        if (!excistedItem.getOwnerId().equals(ownerId)) {
            log.error("Access denied for ownerId {}", ownerId);
            throw new AccessDeniedException("Access denied for ownerId " + ownerId);
        }

        Item item = Item.builder()
                .id(itemId)
                .name(itemDto.getName() != null ? itemDto.getName() : excistedItem.getName())
                .description(itemDto.getDescription() != null
                        ? itemDto.getDescription() : excistedItem.getDescription())
                .available(itemDto.getAvailable() != null
                        ? itemDto.getAvailable() : excistedItem.getAvailable())
                .ownerId(ownerId)
                .itemRequest(itemRequestStorage.findById(itemDto.getItemRequestId()))
                .build();

        itemStorage.update(item);
        log.debug("ItemRequest has been updated: {}", item);

        return item;
    }
}
