package ru.practicum.shareit.booking.controller;

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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private User booker;
    private BookingDto pastBookingDto;
    private BookingDto futureBookingDto;
    private BookingDto rejectedBookingDto;
    private ItemDtoForBooking itemDtoForBookingWithRequest;
    private ItemDtoForBooking itemDtoForBooking;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        booker = InstanceFactory.newUser(4, "booker", "booker@user.com");

        itemDtoForBookingWithRequest = InstanceFactory.newItemDtoForBooking(1,
                "itemWithRequest", "good itemWithRequest",
                true, 2, 1);
        itemDtoForBooking = InstanceFactory.newItemDtoForBooking(1,
                "item", "good item",
                true, 2, null);

        LocalDateTime pastStartDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime pastEndDateTime = LocalDateTime.parse(LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureStartDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime currentStartDateTime = LocalDateTime.parse(LocalDateTime.now().minusWeeks(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime currentEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusWeeks(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        pastBookingDto = InstanceFactory.newBookingDto(1, pastStartDateTime,
                pastEndDateTime, BookingStatus.APPROVED, UserMapper.mapToUserDto(booker),
                itemDtoForBookingWithRequest);
        futureBookingDto = InstanceFactory.newBookingDto(4, futureStartDateTime,
                futureEndDateTime, BookingStatus.WAITING, UserMapper.mapToUserDto(booker),
                itemDtoForBookingWithRequest);
        rejectedBookingDto = InstanceFactory.newBookingDto(2, currentStartDateTime,
                currentEndDateTime, BookingStatus.REJECTED, UserMapper.mapToUserDto(booker),
                itemDtoForBooking);
    }

    @SneakyThrows
    @Test
    void findAllByBookerIdAndStatus() {
        int bookerId = 4;

        List<BookingDto> sourceDtoList = List.of(pastBookingDto);

        when(bookingService.findAllByBookerIdAndStatus(bookerId,
                SearchBookingStatus.PAST, 0, 10))
                .thenReturn(sourceDtoList);

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(pastBookingDto.getId())))
                .andExpect(jsonPath("$[0].start", containsString(pastBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$[0].end", containsString(pastBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(pastBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", Matchers
                        .<String>hasToString(booker.toString())))
                .andExpect(jsonPath("$[0].item", Matchers
                        .<String>hasToString(itemDtoForBookingWithRequest.toString())));
    }

    @SneakyThrows
    @Test
    void findAllByOwnerIdAndStatusThenReturnBookingDto() {
        int ownerId = 2;
        List<BookingDto> sourceDtoList = List.of(rejectedBookingDto);

        when(bookingService.findAllByOwnerIdAndStatus(ownerId,
                SearchBookingStatus.REJECTED, 0, 10))
                .thenReturn(sourceDtoList);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "REJECTED")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(rejectedBookingDto.getId())))
                .andExpect(jsonPath("$[0].start", containsString(rejectedBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$[0].end", containsString(rejectedBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(rejectedBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", Matchers
                        .<String>hasToString(booker.toString())))
                .andExpect(jsonPath("$[0].item", Matchers
                        .<String>hasToString(itemDtoForBooking.toString())));
    }

    @SneakyThrows
    @Test
    void getByIdWhenIdExist() {
        int bookerId = 4;
        int bookingId = 2;

        when(bookingService.findByIdWithValidation(bookingId, bookerId))
                .thenReturn(futureBookingDto);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(futureBookingDto.getId())))
                .andExpect(jsonPath("$.start", containsString(futureBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$.end", containsString(futureBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$.status", is(futureBookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker", Matchers
                        .<String>hasToString(booker.toString())))
                .andExpect(jsonPath("$.item", Matchers
                        .<String>hasToString(itemDtoForBookingWithRequest.toString())));
    }

    @SneakyThrows
    @Test
    void getByIdWhenBookingIdIsNotIntegerThenThrowException() {

        mvc.perform(MockMvcRequestBuilders.get("/bookings/booker")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 4))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentTypeMismatchException));
    }

    @SneakyThrows
    @Test
    void createWhenInputIsCorrect() {
        int bookerId = 4;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd())
                .itemId(futureBookingDto.getItem().getId())
                .build();

        when(bookingService.create(bookingDtoInput, bookerId))
                .thenReturn(futureBookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(futureBookingDto.getId())))
                .andExpect(jsonPath("$.start", containsString(futureBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$.end", containsString(futureBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$.item", Matchers
                        .<String>hasToString(itemDtoForBookingWithRequest.toString())))
                .andExpect(jsonPath("$.booker", Matchers
                        .<String>hasToString(booker.toString())));
    }

    @SneakyThrows
    @Test
    void createWhenStartInPastThenThrowException() {
        int bookerId = 4;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getStart().minusYears(10))
                .end(futureBookingDto.getEnd())
                .itemId(futureBookingDto.getItem().getId())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @SneakyThrows
    @Test
    void patchWhenOriginalStatusWaitingThenSetStatusApproved() {
        int bookingId = 4;
        int ownerId = 2;

        futureBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.patchBooking(bookingId, true, ownerId))
                .thenReturn(futureBookingDto);

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(futureBookingDto.getId())))
                .andExpect(jsonPath("$.start", containsString(futureBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$.end", containsString(futureBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @SneakyThrows
    @Test
    void update() {
        int bookerId = 4;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .id(4)
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd().plusDays(10))
                .itemId(futureBookingDto.getItem().getId())
                .build();

        when(bookingService.update(bookingDtoInput))
                .thenReturn(futureBookingDto);

        mvc.perform(put("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(futureBookingDto.getId())))
                .andExpect(jsonPath("$.start", containsString(futureBookingDto
                        .getStart().toString())))
                .andExpect(jsonPath("$.end", containsString(futureBookingDto
                        .getEnd().toString())))
                .andExpect(jsonPath("$.item", Matchers
                        .<String>hasToString(itemDtoForBookingWithRequest.toString())))
                .andExpect(jsonPath("$.booker", Matchers
                        .<String>hasToString(booker.toString())));
    }

    @SneakyThrows
    @Test
    void delete() {
        doNothing().when(bookingService).delete(1);
        mvc.perform(MockMvcRequestBuilders.delete("/bookings/1"))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .delete(1);
    }
}