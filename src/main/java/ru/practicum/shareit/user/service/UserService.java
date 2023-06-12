package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto create(UserDto userDto);

    UserDto update(User user);

    UserDto updateByPatch(UserDto userDto, Integer userId);

    UserDto delete(Integer id);
}
