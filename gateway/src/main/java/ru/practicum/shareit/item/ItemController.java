package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                               @Valid @PositiveOrZero(message
                                                       = "page should be positive or 0")
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Valid @Positive(message
                                                       = "size should be positive number")
                                               @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Get Items with ownerId={}, from={}, size={}", ownerId, from, size);

        return itemClient.getByOwnerId(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByIdWithOwnerValidation(
            @PathVariable @Validated(ValidationGroups.Create.class) Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get Item with ownerId={}, itemId={}", userId, itemId);

        return itemClient.getByIdWithOwnerValidation(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getBySearchText(@RequestParam(name = "text", required = false) String text,
                                                  @Valid @PositiveOrZero(message
                                                          = "page should be positive or 0")
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Valid @Positive(message
                                                          = "size should be positive number")
                                                  @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Search Items with text={}, from={}, size={}", text, from, size);

        return itemClient.getBySearchText(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(ValidationGroups.Create.class) ItemDtoInput itemDtoInput,
                                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Creating Item {}", itemDtoInput);

        return itemClient.createItem(ownerId, itemDtoInput);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestBody @Validated(ValidationGroups.Create.class) CommentDtoInput commentDtoInput,
            @PathVariable @Validated(ValidationGroups.Create.class) Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Creating Comment {} for Item with ID={}", commentDtoInput, itemId);

        return itemClient.createComment(userId, itemId, commentDtoInput);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemDtoInput itemDtoInput, @PathVariable Integer id,
                                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Update Item {} with ID={} and ownerId={}", itemDtoInput, id, ownerId);

        return itemClient.updateItem(ownerId, id, itemDtoInput);
    }
}
