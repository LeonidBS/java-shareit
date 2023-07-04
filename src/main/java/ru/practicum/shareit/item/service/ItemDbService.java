package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    @Override
    public List<ItemDtoWithComments> findAllByOwnerId(Integer ownerId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(ownerId);

        return itemMapperWithComments.mapListToItemDto(itemRepository
                        .findByOwnerIdOrderById(ownerId, page).toList(),
                ownerId);
    }

    @Override
    @Fetch(FetchMode.JOIN)
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

        @Valid Item item = Item.builder()
                .name(itemDtoInput.getName())
                .description(itemDtoInput.getDescription())
                .available(itemDtoInput.getAvailable())
                .owner(owner)
                .itemRequest(null)
                .build();

        itemRepository.save(item);
        log.debug("Item has been created: {}", item);
        return itemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDtoInput itemDtoInput, Integer ownerId, Integer itemId) {
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
                .name(itemDtoInput.getName() != null ? itemDtoInput.getName() : optionalExistedItem.get().getName())
                .description(itemDtoInput.getDescription() != null
                        ? itemDtoInput.getDescription() : optionalExistedItem.get().getDescription())
                .available(itemDtoInput.getAvailable() != null
                        ? itemDtoInput.getAvailable() : optionalExistedItem.get().getAvailable())
                .owner(owner)
                .itemRequest(optionalExistedItem.get().getItemRequest())
                .build();

        itemRepository.save(item);
        log.debug("ItemRequest has been updated: {}", item);

        return itemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDtoInput commentDtoInput, Integer itemId, Integer userId) {
        UserDto userDto = userService.findById(userId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} is not exist", itemId);
            throw new IdNotFoundException("There is not Item with ID " + itemId);
        }
        if (bookingRepository.countByBookerIdAndItemIdAndStatusAndEndLessThan(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()) == 0) {
            log.error("User with ID {} cannot comment Item with ID {}", userId, itemId);
            throw new MyValidationException("User with ID " + userId +
                    " cannot comment Item with ID " + itemId);
        }

        if (commentDtoInput.getCreated() == null) {
            commentDtoInput.setCreated(LocalDateTime.now());
        }

        @Valid Comment comment = Comment.builder()
                .text(commentDtoInput.getText())
                .item(optionalItem.get())
                .author(UserMapper.mapToUser(userDto))
                .created(commentDtoInput.getCreated() == null ?
                        LocalDateTime.now() : commentDtoInput.getCreated())
                .build();

        commentRepository.save(comment);
        log.debug("Comment has been created: {}", comment);

        return CommentMapper.mapToDto(comment);
    }

    @Override
    public List<ItemDto> findByItemRequestId(Integer itemRequestId) {
        return itemMapper.mapListToItemDto(itemRepository.findByItemRequestId(itemRequestId));
    }
}
