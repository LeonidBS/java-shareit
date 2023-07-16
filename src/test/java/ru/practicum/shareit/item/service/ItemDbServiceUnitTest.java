package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoInput;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.MyValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemDbServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    @Qualifier("dbService")
    private UserService userDbService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemMapperWithComments itemMapperWithComments;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemDbService itemDbService;

    private User owner;
    private User requestor;
    private User author;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private ItemDto secondItemDto;
    private ItemDtoWithComments itemDtoWithComments;
    private Item item;
    private Item secondItem;

    @BeforeEach
    void setup() {
        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        author = InstanceFactory.newUser(3, "author", "author@user.com");
        itemRequest = InstanceFactory.newItemRequest(1, "request", LocalDateTime.now(), requestor);
        item = InstanceFactory.newItem(1, "item", "good item",
                true, owner, itemRequest);
        secondItem = InstanceFactory.newItem(1, "secondItem", "good secondItem",
                true, owner, null);
        itemDto = InstanceFactory.newItemDto(1, "item", "good item", true,
                2, "owner", null, 1);
        secondItemDto = InstanceFactory.newItemDto(1, "secondItem", "good secondItem", true,
                2, "owner", null, 1);
        itemDtoWithComments = InstanceFactory.newItemDtoWithComments(1, "item", "good item",
                true, null, null, null, 2, "owner",
                itemRequest.getCreated(), 0);
    }

    @Test
    void findByIdWithOwnerValidationWhenItemExistThenReturnItem() {
        int itemId = 1;

        when(itemRepository.findByIdFetch(itemId)).thenReturn(item);
        when(itemMapperWithComments.mapToItemDto(item,
                item.getOwner(), item.getItemRequest(), requestor.getId()))
                .thenReturn(itemDtoWithComments);

        ItemDtoWithComments targetItemDto = itemDbService.findByIdWithOwnerValidation(itemId, requestor.getId());

        assertEquals(itemDtoWithComments, targetItemDto);
        InOrder inOrder = inOrder(userDbService, itemRepository, itemMapperWithComments);
        inOrder.verify(userDbService, times(1))
                .findById(requestor.getId());
        inOrder.verify(itemRepository, times(1))
                .findByIdFetch(itemId);
        inOrder.verify(itemMapperWithComments, times(1))
                .mapToItemDto(item, item.getOwner(), item.getItemRequest(), requestor.getId());

    }

    @Test
    void findBySearchTextWhenTextIsNotEmptyThenReturnTwoPages() {
        Pageable pageable1 = PageRequest.of(0, 1);
        Pageable pageable2 = PageRequest.of(1, 1);
        String text = "item";

        List<Item> list1 = List.of(item);
        Page<Item> page1 = new PageImpl<>(list1, pageable1, 0);
        List<Item> list2 = List.of(secondItem);
        Page<Item> page2 = new PageImpl<>(list2, pageable2, 0);
        List<ItemDto> listDto1 = List.of(itemDto);
        List<ItemDto> listDto2 = List.of(secondItemDto);

        when(itemRepository.findBySearchText(text, pageable1)).thenReturn(page1);
        when(itemMapper.mapListToItemDto(list1)).thenReturn(listDto1);
        when(itemRepository.findBySearchText(text, pageable2)).thenReturn(page2);
        when(itemMapper.mapListToItemDto(list2)).thenReturn(listDto2);

        List<ItemDto> targetItemDtoPage1 = itemDbService.findBySearchText(text, 0, 1);
        List<ItemDto> targetItemDtoPage2 = itemDbService.findBySearchText(text, 1, 1);

        assertEquals(listDto1, targetItemDtoPage1);

        InOrder inOrder = inOrder(itemRepository, itemMapper);
        inOrder.verify(itemRepository, times(1))
                .findBySearchText(text, pageable1);
        inOrder.verify(itemMapper, times(1))
                .mapListToItemDto(list1);

        assertEquals(listDto2, targetItemDtoPage2);

        inOrder = inOrder(itemRepository, itemMapper);
        inOrder.verify(itemRepository, times(1))
                .findBySearchText(text, pageable2);
        inOrder.verify(itemMapper, times(1))
                .mapListToItemDto(list2);

    }

    @Test
    void createUserExist() {
        int ownerId = 2;

        ItemDtoInput itemDtoInput = ItemDtoInput.builder()
                .name("item")
                .description("good item")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        when(userDbService.findById(ownerId)).thenReturn(UserMapper.mapToUserDto(owner));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto targetDto = itemDbService.create(itemDtoInput, ownerId);

        assertEquals(itemDto, targetDto);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository, itemRepository);
        inOrder.verify(userDbService, times(1))
                .findById(ownerId);
        inOrder.verify(itemRequestRepository, times(1))
                .findById(itemRequest.getId());
        inOrder.verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void updateWhenUserIsOwner() {
        int itemId = 1;
        int ownerId = 2;

        ItemDtoInput updatedItemDtoInput = ItemDtoInput.builder()
                .description("updated good item")
                .build();
        ItemDto updatedItemDto = InstanceFactory.newItemDto(1, "item", "updated good item",
                true, 2, "owner", null, 1);

        when(userDbService.findById(ownerId)).thenReturn(UserMapper.mapToUserDto(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);
        when(itemMapper.mapToItemDto(any(Item.class))).thenReturn(updatedItemDto);

        ItemDto targetDto = itemDbService.update(updatedItemDtoInput, ownerId, itemId);

        assertEquals(updatedItemDto, targetDto);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository, itemRepository);
        inOrder.verify(userDbService, times(1))
                .findById(ownerId);
        inOrder.verify(itemRepository, times(1))
                .findById(itemId);
        inOrder.verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void updateWhenUserIsNotOwner() {
        int itemId = 1;
        int ownerId = 5;

        ItemDtoInput updatedItemDtoInput = ItemDtoInput.builder()
                .description("updated good item")
                .build();

        when(userDbService.findById(ownerId)).thenReturn(UserMapper.mapToUserDto(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Executable executable = () -> itemDbService.update(updatedItemDtoInput, ownerId, itemId);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, executable);
        assertEquals("Access denied for ownerId " + ownerId,
                accessDeniedException.getMessage());
    }

    @Test
    void createCommentWhenBookingExist() {
        int userId = 3;
        int itemId = 1;

        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("commnet")
                .build();

        when(userDbService.findById(userId)).thenReturn(UserMapper.mapToUserDto(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.countByBookerIdAndItemIdAndStatusAndEndLessThan(
                anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(1);
        when(commentRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        CommentDto targetDto = itemDbService.createComment(commentDtoInput, itemId, userId);

        assertEquals("commnet", targetDto.getText());
        assertEquals(item.getId(), targetDto.getItemId());
        assertEquals(userId, targetDto.getAuthorId());
        InOrder inOrder = inOrder(userDbService, itemRepository,
                bookingRepository, commentRepository);
        inOrder.verify(userDbService, times(1))
                .findById(userId);
        inOrder.verify(itemRepository, times(1))
                .findById(itemId);
        inOrder.verify(bookingRepository, times(1))
                .countByBookerIdAndItemIdAndStatusAndEndLessThan(
                        anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class));
        inOrder.verify(commentRepository, times(1))
                .save(any());
    }

    @Test
    void createCommentWhenBookingNotExist() {
        int userId = 3;
        int itemId = 1;

        CommentDtoInput commentDtoInput = CommentDtoInput.builder()
                .text("commnet")
                .build();

        when(userDbService.findById(userId)).thenReturn(UserMapper.mapToUserDto(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.countByBookerIdAndItemIdAndStatusAndEndLessThan(
                anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(0);

        Executable executable = () -> itemDbService.createComment(commentDtoInput, itemId, userId);

        MyValidationException myValidationException = assertThrows(MyValidationException.class, executable);
        assertEquals("User with ID " + userId +
                        " cannot comment Item with ID " + itemId,
                myValidationException.getMessage());
        InOrder inOrder = inOrder(userDbService, itemRepository,
                bookingRepository);
        inOrder.verify(userDbService, times(1))
                .findById(userId);
        inOrder.verify(itemRepository, times(1))
                .findById(itemId);
        inOrder.verify(bookingRepository, times(1))
                .countByBookerIdAndItemIdAndStatusAndEndLessThan(
                        anyInt(), anyInt(), any(BookingStatus.class), any(LocalDateTime.class));
    }
}