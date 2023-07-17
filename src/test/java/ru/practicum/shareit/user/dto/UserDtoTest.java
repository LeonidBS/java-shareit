package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void testUserDtoWhenNameAndEmailCorrect() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @SneakyThrows
    @Test
    void testUserDtoSerialization() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @SneakyThrows
    @Test
    void testUserDtoDeserialization() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        UserDto userDtoResult = json.parseObject(mapper.writeValueAsString(userDto));

        assertThat(userDtoResult).isEqualTo(userDto);
    }

    @Test
    void testUserToStringWhenToStringResultCorrect() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        assertThat("{id=1, name=user, email=user@user.com}")
                .isEqualTo(userDto.toString());
    }
}