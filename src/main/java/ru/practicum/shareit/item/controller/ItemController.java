package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
    private final ItemServiceImpl itemService;

    @GetMapping
    public List<ItemDto> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.findOwnerAll(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Integer id) {

        return itemService.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text) {

        return itemService.findBySearchText(text);
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
