package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Qualifier("dbService")
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithComments> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "20") Integer size) {

        return itemService.findByOwnerId(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithComments findById(@PathVariable Integer itemId,
                                        @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.findByIdWithOwnerValidation(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findBySearchText(@RequestParam(required = false) String text,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "20") Integer size) {

        return itemService.findBySearchText(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDtoInput itemDtoInput,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.create(itemDtoInput, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestBody CommentDtoInput commentDtoInput,
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.createComment(commentDtoInput, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDtoInput itemDtoInput, @PathVariable Integer id,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.update(itemDtoInput, ownerId, id);
    }
}
