package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.ItemRequestDbService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestDbService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.findOwn(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllExceptOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "20") Integer size) {

        return itemRequestService.findAllExceptOwn(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Integer requestId,
                                   @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemRequestService.findById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDtoInput dtoInput,
                                 @RequestHeader("X-Sharer-User-Id") Integer requestorId) {

        return itemRequestService.create(dtoInput, requestorId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        itemRequestService.delete(id);
    }
}
