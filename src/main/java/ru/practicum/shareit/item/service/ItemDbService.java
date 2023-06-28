package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@Qualifier("dbService")
public class ItemDbService implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemMapperWithBookings itemMapperWithBookings;

    public ItemDbService(ItemRepository itemRepository,
                         @Qualifier("dbService") UserService userService,
                         ItemMapper itemMapper,
                         ItemMapperWithBookings itemMapperWithBookings) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.itemMapperWithBookings = itemMapperWithBookings;
    }

    @Override
    public List<ItemDtoWithBookings> findAllByOwnerId(Integer ownerId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(ownerId);

        return itemMapperWithBookings.mapListToItemDto(itemRepository.findByOwnerIdOrderById(ownerId, page).toList(),
                ownerId);
    }

    @Override
    public ItemDtoWithBookings findByIdWithOwnerValidation(Integer id, Integer userId) {
        userService.findById(userId);
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} has not been found", id);
            throw new IdNotFoundException("There is no Item with ID: " + id);
        }
            return itemMapperWithBookings.mapToItemDto(optionalItem.get(), userId);
    }

    @Override
    public List<ItemDto> findBySearchText(String text, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (text != null) {
            if (!text.isEmpty()) {
                return itemMapper.mapListToItemDto(itemRepository.findBySearchText(text, page).toList());
            }
        }
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Integer ownerId) {
        User owner = UserMapper.mapToUser(userService.findById(ownerId));

        @Valid Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(null)
                .build();

        itemRepository.save(item);
        log.debug("Item has been created: {}", item);
        return itemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId) {
        User owner = UserMapper.mapToUser(userService.findById(ownerId));
        Optional<Item> optionalExistedItem = itemRepository.findById(itemId);

        if (optionalExistedItem.isEmpty()) {
            log.error("User with ID {} has not been found", itemId);
            throw new IdNotFoundException("There is no User with ID: " + itemId);
        }

        if (!optionalExistedItem.get().getOwner().getId().equals(ownerId)) {
            log.error("Access denied for ownerId {}", ownerId);
            throw new AccessDeniedException("Access denied for ownerId " + ownerId);
        }

        Item item = Item.builder()
                .id(itemId)
                .name(itemDto.getName() != null ? itemDto.getName() : optionalExistedItem.get().getName())
                .description(itemDto.getDescription() != null
                        ? itemDto.getDescription() : optionalExistedItem.get().getDescription())
                .available(itemDto.getAvailable() != null
                        ? itemDto.getAvailable() : optionalExistedItem.get().getAvailable())
                .owner(owner)
                .itemRequest(optionalExistedItem.get().getItemRequest())
                .build();

        itemRepository.save(item);
        log.debug("ItemRequest has been updated: {}", item);

        return itemMapper.mapToItemDto(item);
    }
}
