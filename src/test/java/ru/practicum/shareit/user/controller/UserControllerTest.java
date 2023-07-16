package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private @Qualifier("dbService") UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = UserDto.builder()
                .id(1)
                .name("dto")
                .email("user@user.com")
                .build();

        user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @SneakyThrows
    @Test
    void getAll() {
        List<User> users = List.of(user);

        when(userService.findAll(0, 10))
                .thenReturn(UserMapper.mapListToUserDto(users));

        mvc.perform(get("/users/?from=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
    }

    @SneakyThrows
    @Test
    void getByIdWhenIdExist() {
        when(userService.findById(1))
                .thenReturn(UserMapper.mapToUserDto(user));

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @SneakyThrows
    @Test
    void createWhenUserCorrect() {
        UserDto newUserDto = UserDto.builder()
                .name("dto")
                .email("dto@user.com")
                .build();

        when(userService.create(newUserDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void createWhenNameEmpty() {
        UserDto newUserDto = UserDto.builder()
                .name("")
                .email("dto@user.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(userService, never()).create(newUserDto);
    }

    @SneakyThrows
    @Test
    void createWhenEmailNotCorrect() {
        UserDto newUserDto = UserDto.builder()
                .name("dto")
                .email("dto++@user.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(userService, never()).create(newUserDto);
    }

    @SneakyThrows
    @Test
    void updateWhenUserCorrect() {
        User updatedUser = User.builder()
                .id(1)
                .name("updated")
                .email("updated@user.com")
                .build();
        UserDto updatedUserDto = UserMapper.mapToUserDto(updatedUser);

        when(userService.update(updatedUser))
                .thenReturn(updatedUserDto);

        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(updatedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));
    }


    @SneakyThrows
    @Test
    void updateByPatchWhenUserCorrect() {
        UserDto dto = UserDto.builder()
                .email("updated@user.com")
                .build();
        UserDto updatedDto = UserDto.builder()
                .name("user")
                .email("updated@user.com")
                .build();

        when(userService.updateByPatch(dto, 1))
                .thenReturn(updatedDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updatedDto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @SneakyThrows
    @Test
    void updateByPatchWhenEmailNotCorrect() {
        UserDto newUserDto = UserDto.builder()
                .name("dto")
                .email("dto++@user.com")
                .build();

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(userService, never()).create(newUserDto);
    }

    @SneakyThrows
    @Test
    void delete() {
        doNothing().when(userService).deleteById(1);

        mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteById(1);
    }
}