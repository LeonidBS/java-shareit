package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable String id) {
        try {
            return userService.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
    }

    @PostMapping
    public User create(@RequestBody @Validated(ValidationGroups.Create.class) UserDto userDto) {

        return userService.create(userDto);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        return userService.update(user);
    }

    @PatchMapping("/{id}")
    public User updateByPatch(@Valid @RequestBody UserDto userDto, @PathVariable String id) {

        try {
            return userService.updateByPatch(userDto, Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }

    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable String id) {
        try {
            return userService.delete(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("The passed ID: {} is not Integer", id);
            throw new MyValidationException(String.format("The passed ID: %s is not Integer", id));
        }
    }
}
