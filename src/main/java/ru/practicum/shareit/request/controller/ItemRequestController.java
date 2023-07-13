package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.ItemRequestDbService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestDbService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.findOwn(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllExceptOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                 @Valid @PositiveOrZero(message
                                                         = "page should be positive or 0")
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @Valid @Positive(message
                                                         = "size should be positive number")
                                                 @RequestParam(defaultValue = "20") Integer size) {

        return itemRequestService.findAllExceptOwn(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Integer requestId,
                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemRequestService.getById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody @Validated(ValidationGroups.Create.class) ItemRequestDtoInput dtoInput,
                                 @RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.create(dtoInput, requestorId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        itemRequestService.delete(id);
    }
}
