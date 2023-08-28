package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        if (user != null) {
            return new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        } else {
            return null;
        }
    }

    public static User mapToUser(UserDto userDto) {
        if (userDto != null) {
            return new User(
                    userDto.getId(),
                    userDto.getName(),
                    userDto.getEmail()
            );
        } else {
            return null;
        }
    }

    public static List<UserDto> mapListToUserDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(mapToUserDto(user));
        }
        return usersDto;
    }
}
