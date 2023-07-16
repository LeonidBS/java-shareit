package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.service.ItemRequestDbService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestDbService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User requestor;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        itemDto = InstanceFactory.newItemDto(1, "itemDto", "good itemDto", true,
                2, "owner", null, 1);
        itemRequestDto = InstanceFactory.newItemRequestDto(1, "request", LocalDateTime.now(), 1,
                "requestor", List.of(itemDto));
    }

    @SneakyThrows
    @Test
    void findOwnWhenUserExistThenReturnRequests() {
        int userID = 1;
        String itemsString = mapper.writeValueAsString(List.of(itemDto));

        when(itemRequestService.findOwn(userID))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", containsString(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestDto.getRequestorId())))
                .andExpect(jsonPath("$[0].requestorName", is(itemRequestDto.getRequestorName())))
                .andExpect(jsonPath("$[0].items", Matchers.<String>hasToString(itemsString)));
    }

    @SneakyThrows
    @Test
    void findAllExceptOwnWhenUserExistThenReturnRequests() {
        int userID = 3;
        String itemsString = mapper.writeValueAsString(List.of(itemDto));

        when(itemRequestService.findAllExceptOwn(userID, 0, 10))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", containsString(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestDto.getRequestorId())))
                .andExpect(jsonPath("$[0].requestorName", is(itemRequestDto.getRequestorName())))
                .andExpect(jsonPath("$[0].items", Matchers.<String>hasToString(itemsString)));
    }

    @SneakyThrows
    @Test
    void getByIdWhenRequestExist() {
        int userID = 1;
        int requestID = 1;
        String itemsString = mapper.writeValueAsString(List.of(itemDto));

        when(itemRequestService.getById(requestID, userID))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.get("/requests/" + requestID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", containsString(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId())))
                .andExpect(jsonPath("$.requestorName", is(itemRequestDto.getRequestorName())))
                .andExpect(jsonPath("$.items", Matchers.<String>hasToString(itemsString)));
    }

    @SneakyThrows
    @Test
    void createWhenInputIsCorrect() {
        int requestorID = 1;
        ItemRequestDtoInput itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("new itemRequest")
                .build();
        LocalDateTime created = LocalDateTime.now();

        when(itemRequestService.create(itemRequestDtoInput, requestorID))
                .thenReturn(ItemRequestDto.builder()
                        .id(1)
                        .description(itemRequestDtoInput.getDescription())
                        .requestorId(requestorID)
                        .requestorName(requestor.getName())
                        .created(created)
                        .build()
                );

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .header("X-Sharer-User-Id", requestorID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemRequestDtoInput.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(requestorID)))
                .andExpect(jsonPath("$.requestorName", is(requestor.getName())))
                .andExpect(jsonPath("$.created", containsString(created.toString())));
    }
}