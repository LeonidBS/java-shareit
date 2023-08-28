package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> findAll(int from, int size);

    UserDto findById(Integer id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto updateByPatch(UserDto userDto, Integer userId);

    void deleteById(Integer id);
}
