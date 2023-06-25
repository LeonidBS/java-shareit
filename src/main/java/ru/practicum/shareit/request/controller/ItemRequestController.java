package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceImpl itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAll() {
        return itemRequestService.findAll();
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable Integer id) {

        return itemRequestService.findById(id);
    }

    @PostMapping
    public ItemRequest create(@Valid @RequestBody ItemRequest itemRequest) {

        return itemRequestService.create(itemRequest);
    }

    @PutMapping
    public ItemRequest update(@Valid @RequestBody ItemRequest itemRequest) {

        return itemRequestService.update(itemRequest);
    }

    @DeleteMapping("/{id}")
    public ItemRequest delete(@PathVariable Integer id) {

        return itemRequestService.delete(id);
    }
}
