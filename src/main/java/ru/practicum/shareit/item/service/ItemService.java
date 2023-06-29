package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDtoWithComments> findAllByOwnerId(Integer ownerId, int from, int size);

    ItemDtoWithComments findByIdWithOwnerValidation(Integer itemId, Integer userId);

    List<ItemDto> findBySearchText(String text, int from, int size);

    ItemDto create(ItemDto itemDto, Integer ownerId);

    ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId);

    CommentDto createComment(CommentDtoInput commentDtoInput, Integer itemId, Integer userId);
}
