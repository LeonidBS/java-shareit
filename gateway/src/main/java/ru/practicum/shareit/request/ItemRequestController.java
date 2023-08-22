package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(USER_ID) Integer requestorId) {
        log.info("Get ItemRequests with requestorId={}", requestorId);

        return itemRequestClient.getOwn(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllExceptOwn(@RequestHeader(USER_ID) Integer requestorId,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                  @PositiveOrZero(message
                                                          = "page should be positive or 0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "20")
                                                  @Positive(message
                                                          = "size should be positive number") Integer size) {
        log.info("Get ItemRequests with from={}, size={} except ones with requestorId {}",
                from, size, requestorId);

        return itemRequestClient.getAllExceptOwn(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Integer requestId,
                                          @RequestHeader(USER_ID) Integer userId) {
        log.info("Get ItemRequest with ID={}", requestId);

        return itemRequestClient.getById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestBody @Validated ItemRequestDtoInput itemRequestDtoInput,
            @RequestHeader(USER_ID) Integer requestorId) {
        log.info("Create ItemRequest {} by requestorId={}", itemRequestDtoInput, requestorId);

        return itemRequestClient.createRequest(requestorId, itemRequestDtoInput);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        log.info("Delete ItemRequest with ID={}", id);

        return itemRequestClient.deleteRequest(id);
    }
}
