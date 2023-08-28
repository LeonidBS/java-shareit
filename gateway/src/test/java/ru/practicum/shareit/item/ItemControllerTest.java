package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoInput;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @SneakyThrows
    @Test
    void createWhenOwnerExist() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("item")
                .description("good item")
                .available(true)
                .requestId(1)
                .build();

        when(itemClient.createItem(ownerId, itemDtoInput))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @SneakyThrows
    @Test
    void createWhenNameIsEmptyThenThrowException() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("")
                .description("good item")
                .available(true)
                .requestId(1)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenNameIsNullThenThrowException() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .description("good item")
                .available(true)
                .requestId(1)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenDescriptionIsNullThenThrowException() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("item")
                .available(true)
                .requestId(1)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenDescriptionIsEmptyThenThrowException() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("item")
                .description("")
                .available(true)
                .requestId(1)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createWhenAvailableIsNullThenThrowException() {
        int ownerId = 2;
        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("item")
                .description("good item")
                .requestId(1)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header(USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @SneakyThrows
    @Test
    void createCommentWhenTextCorrect() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("comment")
                .created(LocalDateTime.parse(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(CommentDtoInput.DATE_PATTERN))))
                .build();

        when(itemClient.createComment(authorId, itemId, commentDtoInput))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header(USER_ID, authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @SneakyThrows
    @Test
    void createCommentWhenTextEmptyThrowException() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("")
                .created(LocalDateTime.now())
                .build();

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header(USER_ID, authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void createCommentWhenTextNullThenThrowException() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .created(LocalDateTime.now())
                .build();

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header(USER_ID, authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }
}