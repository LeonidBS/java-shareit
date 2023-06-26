package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User mapToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

        public static List<UserDto> mapListToUserDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(mapToUserDto(user));
        }
        return usersDto;
    }
}
