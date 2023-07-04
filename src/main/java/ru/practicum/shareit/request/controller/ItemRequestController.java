package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.ItemRequestDbService;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestDbService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findAllOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.findAllOwn(requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllExceptOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "20")  Integer size) {

        return itemRequestService.findAllExceptOwn(requestorId, from, size);
    }
    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable Integer id) {

        return itemRequestService.getById(id);
    }


    @PostMapping
    public ItemRequestDto create(@RequestBody @Validated(ValidationGroups.Create.class) ItemRequestDtoInput dtoInput,
                              @RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.create(dtoInput, requestorId);
    }

//    @PutMapping
//    public ItemRequest update(@Valid @RequestBody ItemRequest itemRequest) {
//
//        return itemRequestService.update(itemRequest);
//    }
//
//    @DeleteMapping("/{id}")
//    public ItemRequest delete(@PathVariable Integer id) {
//
//        return itemRequestService.delete(id);
//    }
}
