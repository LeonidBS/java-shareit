package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
    }

    @SneakyThrows
    @Test
    void createWhenInputIsCorrect() {
        int requestorID = 1;
        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("new itemRequest")
                .build();

        when(itemRequestClient.createRequest(requestorID, itemRequestDtoInput))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .header(USER_ID, requestorID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @SneakyThrows
    @Test
    void createWhenDescriptionIsEmptyThrowException() {
        int requestorID = 1;
        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("")
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .header(USER_ID, requestorID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenDescriptionIsNullThrowException() {
        int requestorID = 1;
        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .header(USER_ID, requestorID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenDateInPastThenThrowException() {
        int requestorID = 1;
        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("")
                .requestDate(LocalDateTime.now().minusMonths(5))
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .header(USER_ID, requestorID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }
}


