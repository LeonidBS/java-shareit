package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoInput;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(name = "from", defaultValue = "0")
                                         @PositiveOrZero(message
                                                 = "page should be positive or 0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "20")
                                         @Positive(message
                                                 = "size should be positive number") Integer size) {
        log.info("Get Users with from={}, size={}", from, size);

        return userClient.getUsers(from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        log.info("Get User with ID={}", id);

        return userClient.getUsers(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(ValidationGroups.Create.class) UserDtoInput
                                                 userRequestDto) {
        log.info("Creating User {}", userRequestDto);

        return userClient.createUser(userRequestDto);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody @Validated(ValidationGroups.Create.class) UserDtoInput userRequestDto) {
        log.info("Updating User {}", userRequestDto);

        return userClient.updateUser(userRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateByPatch(@RequestBody @Valid UserDtoInput userRequestDto,
                                                @PathVariable Integer id) {
        log.info("Updating User {}, userID {} ", userRequestDto, id);

        return userClient.updateUserByPatch(userRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        log.info("Delete User with ID {} ", id);

        return userClient.deleteUser(id);
    }
}