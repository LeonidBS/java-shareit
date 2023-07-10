package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("dbService") UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {

        return userService.findAll(from, size);
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
    public UserDto update(@RequestBody User user) {

        return userService.update(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateByPatch(@RequestBody @Validated UserDto userDto, @PathVariable Integer id) {

        return userService.updateByPatch(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        userService.deleteById(id);
    }
}
