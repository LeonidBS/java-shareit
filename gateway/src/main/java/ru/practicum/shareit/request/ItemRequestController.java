package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        log.info("Get ItemRequests with requestorId={}", requestorId);

        return itemRequestClient.getOwn(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllExceptOwn(@RequestHeader("X-Sharer-User-Id") Integer requestorId,
                                                  @Valid @PositiveOrZero(message
                                                          = "page should be positive or 0")
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @Valid @Positive(message
                                                          = "size should be positive number")
                                                  @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get ItemRequests with from={}, size={} except ones with requestorId {}",
                from, size, requestorId);

        return itemRequestClient.getAllExceptOwn(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Integer requestId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get ItemRequest with ID={}", requestId);

        return itemRequestClient.getById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestBody @Validated(ValidationGroups.Create.class) ItemRequestDtoInput itemRequestDtoInput,
            @RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        log.info("Create ItemRequest {} by requestorId={}", itemRequestDtoInput, requestorId);

        return itemRequestClient.createRequest(requestorId, itemRequestDtoInput);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        log.info("Delete ItemRequest with ID={}", id);

        return itemRequestClient.deleteRequest(id);
    }
}
