package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
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
    private final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) {

        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(ValidationGroups.Create.class) UserDto userDto) {

        return userService.create(userDto);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody User user) {

        return userService.update(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateByPatch(@Valid @RequestBody UserDto userDto, @PathVariable Integer id) {

        return userService.updateByPatch(userDto, id);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable Integer id) {

        return userService.delete(id);
    }
}
