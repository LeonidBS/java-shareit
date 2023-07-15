package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@Qualifier("dbService")
@RequiredArgsConstructor
public class ItemDbService implements ItemService {

    private final ItemRepository itemRepository;
    @Qualifier("dbService")
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemMapperWithComments itemMapperWithComments;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoWithComments> findByOwnerId(Integer ownerId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(ownerId);

        return itemMapperWithComments.mapListToItemDto(itemRepository
                        .findByOwnerIdOrderById(ownerId, page).toList(),
                ownerId);
    }

    @Override
    public ItemDtoWithComments findByIdWithOwnerValidation(Integer id, Integer userId) {
        userService.findById(userId);

        Item item = itemRepository.findByIdFetch(id);

        if (item == null) {
            log.error("Item with ID {} has not been found", id);
            throw new IdNotFoundException("There is no Item with ID: " + id);
        }

        return itemMapperWithComments.mapToItemDto(item, item.getOwner(),
                item.getItemRequest(), userId);
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
    public ItemDto create(ItemDtoInput itemDtoInput, Integer ownerId) {
        User owner = UserMapper.mapToUser(userService.findById(ownerId));
        ItemRequest itemRequest = null;

        if (itemDtoInput.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDtoInput.getRequestId())
                    .orElseThrow(() -> new IdNotFoundException("ItemRequest not found"));
        }

        @Valid Item item = Item.builder()
                .name(itemDtoInput.getName())
                .description(itemDtoInput.getDescription())
                .available(itemDtoInput.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .build();

        log.debug("Item has been created: {}", item);

        return itemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDtoInput itemDtoInput, Integer ownerId, Integer itemId) {
        User owner = UserMapper.mapToUser(userService.findById(ownerId));

        Item existedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with ID {} has not been found", itemId);
                    return new IdNotFoundException("There is no Item with ID: " + itemId);
                });

        if (!existedItem.getOwner().getId().equals(ownerId)) {
            log.error("Access denied for ownerId {}", ownerId);
            throw new AccessDeniedException("Access denied for ownerId " + ownerId);
        }

        Item item = Item.builder()
                .id(itemId)
                .name(itemDtoInput.getName() != null ? itemDtoInput.getName() : existedItem.getName())
                .description(itemDtoInput.getDescription() != null
                        ? itemDtoInput.getDescription() : existedItem.getDescription())
                .available(itemDtoInput.getAvailable() != null
                        ? itemDtoInput.getAvailable() : existedItem.getAvailable())
                .owner(owner)
                .itemRequest(existedItem.getItemRequest())
                .build();


        log.debug("ItemRequest has been updated: {}", item);

        return itemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDtoInput commentDtoInput, Integer itemId, Integer userId) {
        UserDto userDto = userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item with ID {} is not exist", itemId);
                    return new IdNotFoundException("There is not Item with ID " + itemId);
                });

        if (bookingRepository.countByBookerIdAndItemIdAndStatusAndEndLessThan(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()) == 0) {
            log.error("User with ID {} cannot comment Item with ID {}", userId, itemId);
            throw new MyValidationException("User with ID " + userId +
                    " cannot comment Item with ID " + itemId);
        }

        Comment comment = Comment.builder()
                .text(commentDtoInput.getText())
                .item(item)
                .author(UserMapper.mapToUser(userDto))
                .created(commentDtoInput.getCreated() == null ?
                        LocalDateTime.now() : commentDtoInput.getCreated())
                .build();

        log.debug("Comment has been created: {}", comment);

        return CommentMapper.INSTANCE.mapToDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> findByItemRequestId(Integer itemRequestId) {

        return itemMapper.mapListToItemDto(itemRepository.findByItemRequestId(itemRequestId));
    }
}
