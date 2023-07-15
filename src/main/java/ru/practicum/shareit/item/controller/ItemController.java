package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    @Qualifier("dbService")
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithComments> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                   @Valid @PositiveOrZero(message
                                                           = "page should be positive or 0")
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @Valid @Positive(message
                                                           = "size should be positive number")
                                                       @RequestParam(defaultValue = "20") Integer size) {

        return itemService.findByOwnerId(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithComments findById(@PathVariable @Validated(ValidationGroups.Create.class) Integer itemId,
                                        @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.findByIdWithOwnerValidation(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text,
                                         @Valid @PositiveOrZero(message
                                                 = "page should be positive or 0")
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @Valid @Positive(message
                                                 = "size should be positive number")
                                             @RequestParam(defaultValue = "20") Integer size) {

        return itemService.findBySearchText(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(ValidationGroups.Create.class) ItemDtoInput itemDtoInput,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.create(itemDtoInput, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestBody @Validated(ValidationGroups.Create.class) CommentDtoInput commentDtoInput,
            @PathVariable @Validated(ValidationGroups.Create.class) Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.createComment(commentDtoInput, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody ItemDtoInput itemDtoInput, @PathVariable Integer id,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.update(itemDtoInput, ownerId, id);
    }


}
