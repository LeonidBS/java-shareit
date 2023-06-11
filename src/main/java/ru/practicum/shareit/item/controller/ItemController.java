package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.findOwnerAll(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable String id) {
        try {
            return itemService.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text) {

        return itemService.findBySearchText(text);
    }

    @PostMapping
    public Item create(@RequestBody @Validated(ValidationGroups.Create.class) ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public Item update(@Valid @RequestBody ItemDto itemDto, @PathVariable String id,
                       @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        try {
            return itemService.update(itemDto, ownerId, Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
    }


}
