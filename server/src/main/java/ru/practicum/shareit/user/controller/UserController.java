package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Qualifier("dbService")
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "20") Integer size) {

        return userService.findAll(from, size);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) {

        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {

        return userService.create(userDto);
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto userDto) {

        return userService.update(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateByPatch(@RequestBody UserDto userDto, @PathVariable Integer id) {

        return userService.updateByPatch(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {

        userService.deleteById(id);
    }
}
