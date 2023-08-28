package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDtoInput;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @SneakyThrows
    @Test
    void createWhenUserCorrect() {
        UserDtoInput userDto = UserDtoInput.builder()
                .id(1)
                .name("dto")
                .email("user@user.com")
                .build();

        when(userClient.createUser(userDto))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @SneakyThrows
    @Test
    void createWhenNameEmpty() {
        UserDtoInput newUserDto = UserDtoInput.builder()
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
    }

    @SneakyThrows
    @Test
    void createWhenEmailNotCorrect() {
        UserDtoInput newUserDto = UserDtoInput.builder()
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
    }

    @SneakyThrows
    @Test
    void updateByPatchWhenUserCorrect() {
        UserDtoInput dto = UserDtoInput.builder()
                .email("updated@user.com")
                .build();

        when(userClient.updateUserByPatch(dto, 1))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @SneakyThrows
    @Test
    void updateByPatchWhenEmailNotCorrect() {
        UserDtoInput newUserDto = UserDtoInput.builder()
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
    }
}