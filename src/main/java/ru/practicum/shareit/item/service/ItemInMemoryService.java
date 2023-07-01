package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentsInMemoryRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemInMemoryRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Qualifier("inMemoryService")
@RequiredArgsConstructor
public class ItemInMemoryService implements ItemService {
    private final ItemInMemoryRepository itemInMemoryRepository;
    @Qualifier("inMemoryService")
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemMapperWithComments itemMapperWithComments;
    private final CommentsInMemoryRepository commentsInMemoryRepository;

    @Override
    public List<ItemDtoWithComments> findAllByOwnerId(Integer ownerId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        userService.findById(ownerId);

        return itemMapperWithComments.mapListToItemDto(itemInMemoryRepository.findAllByUserId(ownerId, page).toList(),
                ownerId);
    }

    @Override
    public ItemDtoWithComments findByIdWithOwnerValidation(Integer id, Integer userId) {
        Optional<Item> optionalItem = itemInMemoryRepository.findById(id);

        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} has not been found", id);
            throw new IdNotFoundException("There is no Item with ID: " + id);
        }

        return itemMapperWithComments.mapToItemDto(optionalItem.get(),
                optionalItem.get().getOwner(),
                optionalItem.get().getItemRequest(), userId);
    }

    @Override
    public List<ItemDto> findBySearchText(String text, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (text != null) {
            if (!text.isEmpty()) {
                return itemMapper.mapListToItemDto(itemInMemoryRepository.findBySearchText(text, page).toList());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        User owner = UserMapper.mapToUser(userService.findById(userId));

        if (itemDto.getAvailable() == null) {
            log.error("Available is NULL");
            throw new MyValidationException("Available is NULL");
        }

        @Valid Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(null)
                .build();

        itemInMemoryRepository.save(item);
        log.debug("ItemRequest has been created: {}", item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId) {
        User owner = UserMapper.mapToUser(userService.findById(ownerId));
        Optional<Item> optionalExistedItem = itemInMemoryRepository.findById(itemId);

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

        itemInMemoryRepository.save(item);
        log.debug("ItemRequest has been updated: {}", item);

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public CommentDto createComment(CommentDtoInput commentDtoInput, Integer itemId, Integer userId) {
        UserDto userDto = userService.findById(userId);

        Optional<Item> optionalItem = itemInMemoryRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} is not exist", itemId);
            throw new IdNotFoundException("There is not Item with ID " + itemId);
        }
        @Valid Comment comment = Comment.builder()
                .text(commentDtoInput.getText())
                .item(optionalItem.get())
                .author(UserMapper.mapToUser(userDto))
                .created(commentDtoInput.getCreated())
                .build();

        return CommentMapper.mapToDto(commentsInMemoryRepository.save(comment));
    }
}
