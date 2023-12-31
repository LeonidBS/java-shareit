package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.SearchBookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ApprovingException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingDbServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    @Qualifier("dbService")
    private UserService userDbService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingDbService bookingDbService;

    private User requestor;
    private User booker;
    private Item itemWithRequest;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking presentBooking;
    private Booking rejectedBooking;
    private BookingDto pastBookingDto;
    private BookingDto futureBookingDto;
    private BookingDto presentBookingDto;
    private BookingDto rejectedBookingDto;
    private ItemDtoForBooking itemDtoForBookingWithRequest;
    private ItemDtoForBooking itemDtoForBooking;

    @BeforeEach
    void setUp() {
        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        User owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        User author = InstanceFactory.newUser(3, "author", "author@user.com");
        booker = InstanceFactory.newUser(4, "booker", "booker@user.com");

        ItemRequest itemRequest = InstanceFactory.newItemRequest(1, "request", LocalDateTime.now(), requestor);
        itemWithRequest = InstanceFactory.newItem(1, "itemWithRequest",
                "good itemWithRequest", true, owner, itemRequest);
        Item item = InstanceFactory.newItem(1, "item", "good item",
                true, owner, null);
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

        pastBooking = InstanceFactory.newBooking(1, pastStartDateTime,
                pastEndDateTime, itemWithRequest, booker, BookingStatus.APPROVED);
        futureBooking = InstanceFactory.newBooking(4, futureStartDateTime,
                futureEndDateTime, itemWithRequest, requestor, BookingStatus.WAITING);
        presentBooking = InstanceFactory.newBooking(3, currentStartDateTime,
                currentEndDateTime, item, author, BookingStatus.APPROVED);
        rejectedBooking = InstanceFactory.newBooking(2, currentStartDateTime,
                currentEndDateTime, item, booker, BookingStatus.REJECTED);

        pastBookingDto = InstanceFactory.newBookingDto(1, pastStartDateTime,
                pastEndDateTime, BookingStatus.APPROVED, UserMapper.mapToUserDto(booker),
                itemDtoForBookingWithRequest);
        futureBookingDto = InstanceFactory.newBookingDto(4, futureStartDateTime,
                futureEndDateTime, BookingStatus.WAITING, UserMapper.mapToUserDto(requestor),
                itemDtoForBookingWithRequest);
        presentBookingDto = InstanceFactory.newBookingDto(3, currentStartDateTime,
                currentEndDateTime, BookingStatus.APPROVED, UserMapper.mapToUserDto(author),
                itemDtoForBooking);
        rejectedBookingDto = InstanceFactory.newBookingDto(2, currentStartDateTime,
                currentEndDateTime, BookingStatus.REJECTED, UserMapper.mapToUserDto(booker),
                itemDtoForBooking);

    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void findAllByBookerIdAndStatus(String stateString) {
        int bookerId = 4;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from, size);
        PageRequest page = PageRequest.of(from, size);
        List<BookingDto> sourceListDto;
        List<Booking> sourceList;
        Page<Booking> sourcePage;
        List<BookingDto> targetListDto;

        switch (stateString) {
            case "ALL":
                sourceListDto = List.of(pastBookingDto, futureBookingDto,
                        presentBookingDto, rejectedBookingDto);
                sourceList = List.of(pastBooking, futureBooking,
                        presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdOrderByStartDesc(
                        bookerId, page)).thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.ALL,
                                from, size);

                assertEquals(sourceListDto.get(0), targetListDto.get(0));
                assertEquals(sourceListDto.get(1), targetListDto.get(1));
                assertEquals(sourceListDto.get(2), targetListDto.get(2));
                assertEquals(sourceListDto.get(3), targetListDto.get(3));
                verify(bookingRepository, times(1))
                        .findByBookerIdOrderByStartDesc(
                                bookerId, page);
                return;

            case "CURRENT":
                sourceListDto = List.of(presentBookingDto, rejectedBookingDto);
                sourceList = List.of(presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                        eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.CURRENT,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                                eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page));
                return;

            case "PAST":
                sourceListDto = List.of(pastBookingDto);
                sourceList = List.of(pastBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdAndEndLessThanOrderByEndDesc(
                        eq(bookerId), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.PAST,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByBookerIdAndEndLessThanOrderByEndDesc(
                                eq(bookerId), any(LocalDateTime.class), eq(page));
                return;

            case "FUTURE":
                sourceListDto = List.of(futureBookingDto);
                sourceList = List.of(futureBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(
                        eq(bookerId), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.FUTURE,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByBookerIdAndStartGreaterThanOrderByStartDesc(
                                eq(bookerId), any(LocalDateTime.class), eq(page));
                return;

            case "WAITING":
                sourceListDto = List.of(futureBookingDto);
                sourceList = List.of(futureBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.WAITING, page))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.WAITING,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.WAITING, page);
                return;

            case "REJECTED":
                sourceListDto = List.of(pastBookingDto, futureBookingDto,
                        presentBookingDto, rejectedBookingDto);
                sourceList = List.of(pastBooking, futureBooking,
                        presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.REJECTED, page))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByBookerIdAndStatus(bookerId, SearchBookingStatus.REJECTED,
                                from, size);

                assertEquals(sourceListDto.get(0), targetListDto.get(0));
                assertEquals(sourceListDto.get(1), targetListDto.get(1));
                assertEquals(sourceListDto.get(2), targetListDto.get(2));
                assertEquals(sourceListDto.get(3), targetListDto.get(3));
                verify(bookingRepository, times(1))
                        .findByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.REJECTED, page);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void findAllByOwnerIdAndStatus(String stateString) {
        int ownerId = 2;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from, size);
        PageRequest page = PageRequest.of(from, size);
        List<BookingDto> sourceListDto;
        List<Booking> sourceList;
        Page<Booking> sourcePage;
        List<BookingDto> targetListDto;

        switch (stateString) {
            case "ALL":
                sourceListDto = List.of(pastBookingDto, futureBookingDto,
                        presentBookingDto, rejectedBookingDto);
                sourceList = List.of(pastBooking, futureBooking,
                        presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdOrderByStartDesc(
                        ownerId, page)).thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.ALL,
                                from, size);

                assertEquals(sourceListDto.get(0), targetListDto.get(0));
                assertEquals(sourceListDto.get(1), targetListDto.get(1));
                assertEquals(sourceListDto.get(2), targetListDto.get(2));
                assertEquals(sourceListDto.get(3), targetListDto.get(3));
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdOrderByStartDesc(
                                ownerId, page);
                return;

            case "CURRENT":
                sourceListDto = List.of(presentBookingDto, rejectedBookingDto);
                sourceList = List.of(presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                        eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.CURRENT,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
                                eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(page));
                return;

            case "PAST":
                sourceListDto = List.of(pastBookingDto);
                sourceList = List.of(pastBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdAndEndLessThanOrderByEndDesc(
                        eq(ownerId), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.PAST,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdAndEndLessThanOrderByEndDesc(
                                eq(ownerId), any(LocalDateTime.class), eq(page));
                return;

            case "FUTURE":
                sourceListDto = List.of(futureBookingDto);
                sourceList = List.of(futureBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(
                        eq(ownerId), any(LocalDateTime.class), eq(page)))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.FUTURE,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(
                                eq(ownerId), any(LocalDateTime.class), eq(page));
                return;

            case "WAITING":
                sourceListDto = List.of(futureBookingDto);
                sourceList = List.of(futureBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.WAITING, page))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.WAITING,
                                from, size);

                assertEquals(sourceListDto, targetListDto);
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.WAITING, page);
                return;

            case "REJECTED":
                sourceListDto = List.of(pastBookingDto, futureBookingDto,
                        presentBookingDto, rejectedBookingDto);
                sourceList = List.of(pastBooking, futureBooking,
                        presentBooking, rejectedBooking);
                sourcePage = new PageImpl<>(sourceList, pageable, 0);

                when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, BookingStatus.REJECTED, page))
                        .thenReturn(sourcePage);

                targetListDto = bookingDbService
                        .findAllByOwnerIdAndStatus(ownerId, SearchBookingStatus.REJECTED,
                                from, size);

                assertEquals(sourceListDto.get(0), targetListDto.get(0));
                assertEquals(sourceListDto.get(1), targetListDto.get(1));
                assertEquals(sourceListDto.get(2), targetListDto.get(2));
                assertEquals(sourceListDto.get(3), targetListDto.get(3));
                verify(bookingRepository, times(1))
                        .findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.REJECTED, page);
        }
    }

    @Test
    void findByIdWithValidationWhenUserIsOwnerOrBookerThenReturnBookingDto() {
        int bookingId = 1;
        int bookerId = 4;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(pastBooking));

        BookingDto targetDto = bookingDbService.findByIdWithValidation(bookingId, bookerId);

        assertEquals(pastBookingDto, targetDto);
        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void findByIdWithValidationWhenNoBookingByIdThenThrowException() {
        int bookingId = 99;
        int requestorId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Executable executable = () -> bookingDbService.findByIdWithValidation(bookingId, requestorId);

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, executable);
        assertEquals("There is no Booking with ID: " + bookingId,
                idNotFoundException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void findByIdWithValidationWhenUserIsNotOwnerOrBookingThenThrowException() {
        int bookingId = 1;
        int requestorId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(pastBooking));

        Executable executable = () -> bookingDbService.findByIdWithValidation(bookingId, requestorId);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, executable);
        assertEquals("Access denied for userId " + requestorId,
                accessDeniedException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void createWhenInputIsCorrectAndBookerIsNotOwner() {

        int requestorId = 1;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd())
                .itemId(futureBookingDto.getItem().getId())
                .build();

        when(userDbService.findById(requestorId))
                .thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.of(itemWithRequest));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        BookingDto targetDto = bookingDbService.create(bookingDtoInput, requestorId);

        assertEquals(bookingDtoInput.getStart(), targetDto.getStart());
        assertEquals(bookingDtoInput.getEnd(), targetDto.getEnd());
        assertEquals(itemDtoForBookingWithRequest, targetDto.getItem());
        assertEquals(UserMapper.mapToUserDto(requestor), targetDto.getBooker());
        InOrder inOrder = inOrder(userDbService, itemRepository, bookingRepository);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRepository, times(1))
                .findById(bookingDtoInput.getItemId());
        inOrder.verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void createWhenNoItemAvailableFalseThrowException() {

        int requestorId = 1;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getEnd())
                .end(futureBookingDto.getStart())
                .itemId(futureBookingDto.getItem().getId())
                .build();
        itemWithRequest.setAvailable(false);

        when(userDbService.findById(requestorId))
                .thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.of(itemWithRequest));

        Executable executable = () -> bookingDbService.create(bookingDtoInput, requestorId);

        MyValidationException myValidationException = assertThrows(MyValidationException.class, executable);
        assertEquals("Item (ID " + bookingDtoInput.getItemId() + " is not available",
                myValidationException.getMessage());

        InOrder inOrder = inOrder(userDbService, itemRepository);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRepository, times(1))
                .findById(bookingDtoInput.getItemId());
    }

    @Test
    void createWhenStartAfterEndThenThrowException() {

        int requestorId = 1;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getEnd())
                .end(futureBookingDto.getStart())
                .itemId(futureBookingDto.getItem().getId())
                .build();

        when(userDbService.findById(requestorId))
                .thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.of(itemWithRequest));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        Executable executable = () -> bookingDbService.create(bookingDtoInput, requestorId);

        MyValidationException myValidationException = assertThrows(MyValidationException.class, executable);
        assertEquals("Start must be before End",
                myValidationException.getMessage());

        InOrder inOrder = inOrder(userDbService, itemRepository);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRepository, times(1))
                .findById(bookingDtoInput.getItemId());
    }

    @Test
    void createWhenBookerIsOwnerThenThrowException() {

        int ownerId = 2;

        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd())
                .itemId(futureBookingDto.getItem().getId())
                .build();

        when(userDbService.findById(ownerId))
                .thenReturn(UserMapper.mapToUserDto(booker));
        when(itemRepository.findById(bookingDtoInput.getItemId()))
                .thenReturn(Optional.of(itemWithRequest));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        Executable executable = () -> bookingDbService.create(bookingDtoInput, ownerId);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, executable);
        assertEquals("Item (ID " + bookingDtoInput.getItemId() + " belongs to Booker",
                accessDeniedException.getMessage());

        InOrder inOrder = inOrder(userDbService, itemRepository);
        inOrder.verify(userDbService, times(1))
                .findById(ownerId);
        inOrder.verify(itemRepository, times(1))
                .findById(bookingDtoInput.getItemId());
    }

    @Test
    void patchBookingUserIsOwnerBookingNotApprovedButInWaiting() {
        int bookingId = 4;
        Boolean approved = true;
        int ownerId = 2;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(futureBooking));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        BookingDto targetDto = bookingDbService.patchBooking(bookingId, approved, ownerId);

        assertEquals(futureBooking.getStart(), targetDto.getStart());
        assertEquals(futureBooking.getEnd(), targetDto.getEnd());
        assertEquals(itemDtoForBookingWithRequest, targetDto.getItem());
        assertEquals(UserMapper.mapToUserDto(requestor), targetDto.getBooker());
        assertEquals(BookingStatus.APPROVED, targetDto.getStatus());
        InOrder inOrder = inOrder(bookingRepository, bookingRepository);
        inOrder.verify(bookingRepository, times(1))
                .findById(bookingId);
        inOrder.verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void patchBookingWhenBookingAlreadyApprovedThenThrowException() {
        int bookingId = 3;
        Boolean approved = false;
        int ownerId = 2;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(presentBooking));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        Executable executable = () -> bookingDbService.patchBooking(bookingId, approved, ownerId);

        MyValidationException myValidationException = assertThrows(MyValidationException.class, executable);
        assertEquals("Booking has been approved already",
                myValidationException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void patchBookingWhenNoBookingThenThrowException() {
        int bookingId = 99;
        Boolean approved = false;
        int ownerId = 2;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        Executable executable = () -> bookingDbService.patchBooking(bookingId, approved, ownerId);

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, executable);
        assertEquals("There is no Booking with ID: " + bookingId,
                idNotFoundException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void patchBookingWhenUserIsNotOwnerThenThrowException() {
        int bookingId = 3;
        Boolean approved = false;
        int ownerId = 1;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(presentBooking));

        Executable executable = () -> bookingDbService.patchBooking(bookingId, approved, ownerId);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, executable);
        assertEquals("Access denied for ownerId " + ownerId,
                accessDeniedException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void patchBookingWhenStatusNotWaitingThenThrowException() {
        int bookingId = 2;
        Boolean approved = false;
        int ownerId = 2;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(rejectedBooking));

        Executable executable = () -> bookingDbService.patchBooking(bookingId, approved, ownerId);

        ApprovingException approvingException = assertThrows(ApprovingException.class, executable);
        assertEquals("Booking has not been requested",
                approvingException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void updateBookingWhenInputIsCorrect() {
        int bookingId = 3;

        BookingDtoInput updatedBookingDto = BookingDtoInput.builder()
                .id(presentBooking.getId())
                .start(presentBooking.getStart())
                .end(presentBooking.getEnd().plusDays(3))
                .status(presentBooking.getStatus())
                .itemId(presentBooking.getItem().getId())
                .bookerId(presentBooking.getBooker().getId())
                .build();
        presentBooking.setEnd(updatedBookingDto.getEnd());

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(presentBooking));
        when(itemRepository.findById(updatedBookingDto.getItemId()))
                .thenReturn(Optional.of(presentBooking.getItem()));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        BookingDto targetDto = bookingDbService.update(updatedBookingDto);

        assertEquals(updatedBookingDto.getStart(), targetDto.getStart());
        assertEquals(updatedBookingDto.getEnd(), targetDto.getEnd());
        assertEquals(itemDtoForBooking, targetDto.getItem());
        assertEquals(UserMapper.mapToUserDto(presentBooking.getBooker()),
                targetDto.getBooker());
        assertEquals(BookingStatus.APPROVED, targetDto.getStatus());
        InOrder inOrder = inOrder(bookingRepository, itemRepository, bookingRepository);
        inOrder.verify(bookingRepository, times(1))
                .findById(bookingId);
        inOrder.verify(itemRepository, times(1))
                .findById(updatedBookingDto.getItemId());
        inOrder.verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void updateBookingWhenNoBookingThenThrowException() {
        int bookingId = 99;
        BookingDtoInput updatedBookingDto = BookingDtoInput.builder()
                .id(bookingId)
                .start(presentBooking.getStart())
                .end(presentBooking.getEnd().plusDays(3))
                .status(presentBooking.getStatus())
                .itemId(presentBooking.getItem().getId())
                .bookerId(presentBooking.getBooker().getId())
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        Executable executable = () -> bookingDbService.update(updatedBookingDto);

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, executable);
        assertEquals("There is no Booking with ID: " + updatedBookingDto.getId(),
                idNotFoundException.getMessage());

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void findLastBookingByItemIdWhenLastBookingExistThenReturnBooking() {
        int itemId = 1;

        when(bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartLessThanOrderByStartDesc(
                       eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(pastBooking);
    }

    @Test
    void findNextBookingByItemIdWhenLastBookingExistThenReturnBooking() {
        int itemId = 1;

        when(bookingRepository
                .findFirstBookingByItemIdAndStatusAndStartGreaterThanOrderByStart(
                        eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(pastBooking);
    }
}
