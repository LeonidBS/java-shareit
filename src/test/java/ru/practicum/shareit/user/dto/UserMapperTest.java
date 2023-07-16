package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    User user = User.builder()
            .id(1)
            .name("user")
            .email("user@user.com")
            .build();

    UserDto dto = UserDto.builder()
            .id(1)
            .name("user")
            .email("user@user.com")
            .build();

    @Test
    void mapToUserDto() {
        assertEquals(UserMapper.mapToUserDto(user), dto);
    }

    @Test
    void mapToUser() {
        assertEquals(UserMapper.mapToUser(dto), user);
    }

    @Test
    void mapListToUserDto() {
        List<User> users = new ArrayList<>();
        List<UserDto> dtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            users.add(User.builder()
                    .id(i + 1)
                    .name("user" + i)
                    .email("user" + i + "@user.com")
                    .build());

            dtos.add(UserDto.builder()
                    .id(i + 1)
                    .name("user" + i)
                    .email("user" + i + "@user.com")
                    .build());
        }

        assertEquals(UserMapper.mapListToUserDto(users), dtos);
    }
}