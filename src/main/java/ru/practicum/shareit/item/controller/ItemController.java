package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("dbService") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        int from = 0;
        int size = 10;

        return itemService.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Integer id) {

        return itemService.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text) {
        int from = 0;
        int size = 10;
        return itemService.findBySearchText(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(ValidationGroups.Create.class) ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody ItemDto itemDto, @PathVariable Integer id,
                          @RequestHeader("X-Sharer-User-Id") Integer ownerId) {

        return itemService.update(itemDto, ownerId, id);
    }


}
