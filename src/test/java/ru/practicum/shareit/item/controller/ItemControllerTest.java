package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User owner;
    private User author;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private ItemDto secondItemDto;
    private ItemDtoWithComments itemDtoWithComments;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private BookingDtoForItem lastBookingDto;
    private BookingDtoForItem nextBookingDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        User requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        author = InstanceFactory.newUser(3, "author", "author@user.com");

        itemRequest = InstanceFactory.newItemRequest(1, "request", LocalDateTime.now(), requestor);
        item = InstanceFactory.newItem(1, "item", "good item",
                true, owner, itemRequest);
        itemDto = InstanceFactory.newItemDto(1, "item", "good item", true,
                2, "owner", null, 1);
        secondItemDto = InstanceFactory.newItemDto(1, "secondItem", "good secondItem", true,
                2, "owner", null, 1);

        comment = InstanceFactory.newComment(1, "comment", item, author, LocalDateTime.now());
        commentDto = InstanceFactory.newCommentDto(1, "comment",
                item.getId(), item.getName(), author.getId(), author.getName(), comment.getCreated());

        LocalDateTime lastBookingStartDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime lastBookingEndDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime nextBookingStartDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime nextBookingEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        lastBookingDto = InstanceFactory.newBookingDtoForItem(1, lastBookingStartDateTime,
                lastBookingEndDateTime, BookingStatus.APPROVED, requestor.getId());
        nextBookingDto = InstanceFactory.newBookingDtoForItem(2, nextBookingStartDateTime,
                nextBookingEndDateTime, BookingStatus.APPROVED, requestor.getId());

        itemDtoWithComments = InstanceFactory.newItemDtoWithComments(1, "item", "good item",
                true, lastBookingDto, nextBookingDto, List.of(commentDto), 2, "owner",
                itemRequest.getCreated(), 0);
    }

    @SneakyThrows
    @Test
    void findByOwnerIdWhenOwnerExistThenReturnItems() {
        int ownerId = 1;

        List<ItemDtoWithComments> itemsListDtoWithComments = List.of(itemDtoWithComments);
        String stringCommentsDtoForItem = mapper.writeValueAsString(List.of(commentDto));

        when(itemService.findByOwnerId(ownerId, 0, 10))
                .thenReturn(itemsListDtoWithComments);

        mvc.perform(MockMvcRequestBuilders.get("/items/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithComments.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithComments.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithComments.getDescription())))
                .andExpect(jsonPath("$[0].lastBooking", Matchers
                        .<String>hasToString(lastBookingDto.toString())))
                .andExpect(jsonPath("$[0].nextBooking", Matchers
                        .<String>hasToString(nextBookingDto.toString())))
                .andExpect(jsonPath("$[0].comments", Matchers.<String>hasToString(stringCommentsDtoForItem)))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoWithComments.getOwnerId())))
                .andExpect(jsonPath("$[0].ownerName", is(itemDtoWithComments.getOwnerName())));
    }

    @SneakyThrows
    @Test
    void findByIdWhenUserIsOwner() {
        int userId = 1;
        int itemId = 1;
        String stringCommentsDtoForItem = mapper.writeValueAsString(List.of(commentDto));

        when(itemService.findByIdWithOwnerValidation(itemId, userId))
                .thenReturn(itemDtoWithComments);

        mvc.perform(MockMvcRequestBuilders.get("/items/" + itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithComments.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithComments.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithComments.getDescription())))
                .andExpect(jsonPath("$.lastBooking", Matchers
                        .<String>hasToString(lastBookingDto.toString())))
                .andExpect(jsonPath("$.nextBooking", Matchers
                        .<String>hasToString(nextBookingDto.toString())))
                .andExpect(jsonPath("$.comments", Matchers.<String>hasToString(stringCommentsDtoForItem)))
                .andExpect(jsonPath("$.ownerId", is(itemDtoWithComments.getOwnerId())))
                .andExpect(jsonPath("$.ownerName", is(itemDtoWithComments.getOwnerName())));
    }

    @SneakyThrows
    @Test
    void getBySearchTextWhenPageParametersCorrectThenReturnItemsDto() {
        String text = "second";
        List<ItemDto> itemsListDto = List.of(secondItemDto);

        when(itemService.findBySearchText(text, 0, 10))
                .thenReturn(itemsListDto);

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(secondItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(secondItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(secondItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(secondItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoWithComments.getOwnerId())))
                .andExpect(jsonPath("$[0].ownerName", is(itemDtoWithComments.getOwnerName())));
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

        when(itemService.create(itemDtoInput, ownerId))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoInput))
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(ownerId)))
                .andExpect(jsonPath("$.ownerName", is(owner.getName())))
                .andExpect(jsonPath("$.requestId", is(itemRequest.getId())));
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
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
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
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
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
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
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
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
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
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
    }


    @SneakyThrows
    @Test
    void createCommentWhenTextCorrect() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("comment")
                .created(comment.getCreated())
                .build();

        when(itemService.createComment(commentDtoInput, itemId, authorId))
                .thenReturn(CommentMapper.INSTANCE.mapToDto(comment));

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header("X-Sharer-User-Id", authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId())))
                .andExpect(jsonPath("$.text", is(commentDtoInput.getText())))
                .andExpect(jsonPath("$.itemId", is(itemId)))
                .andExpect(jsonPath("$.itemName", is(item.getName())))
                .andExpect(jsonPath("$.authorId", is(authorId)))
                .andExpect(jsonPath("$.authorName", is(author.getName())))
                .andExpect(jsonPath("$.created", containsString(commentDtoInput
                        .getCreated().toString())));
    }

    @SneakyThrows
    @Test
    void createCommentWhenTextEmptyThrowException() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("")
                .created(comment.getCreated())
                .build();

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header("X-Sharer-User-Id", authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
    }

    @SneakyThrows
    @Test
    void createCommentWhenTextNullThenThrowException() {
        int authorId = 3;
        int itemId = 1;
        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .created(comment.getCreated())
                .build();

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDtoInput))
                        .header("X-Sharer-User-Id", authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));

        verify(itemService, never()).create(any(), anyInt());
    }

    @SneakyThrows
    @Test
    void updateWhenItemExist() {
        int ownerId = 2;
        int itemId = 1;
        ItemDtoInput updatedItemDtoInput = ItemDtoInput.builder()
                .description("very good item")
                .available(true)
                .build();

        itemDto.setDescription("very good item");

        when(itemService.update(updatedItemDtoInput, ownerId, itemId))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(updatedItemDtoInput))
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDtoInput.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(ownerId)))
                .andExpect(jsonPath("$.ownerName", is(owner.getName())))
                .andExpect(jsonPath("$.requestId", is(itemRequest.getId())));
    }
}