package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ItemDtoWithComments itemDtoWithComments;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setup() {
        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        author = InstanceFactory.newUser(3, "author", "author@user.com");
        itemRequest = InstanceFactory.newItemRequest(1, "request", LocalDateTime.now(), requestor);
        item = InstanceFactory.newItem(1, "item", "good item",
                true, owner, itemRequest);
       itemDto = InstanceFactory.newItemDto(1, "itemDto", "good itemDto", true,
                2, "owner", null, 1);
        comment = InstanceFactory.newComment(1 , "commnet", author, LocalDateTime.now());
        itemDtoWithComments = InstanceFactory.newItemDtoWithComments(1, "item", "good item",
                true,null, null, null, 2, "owner",
                itemRequest.getCreated(), 0);
    }

    @Test
    void findByIdWithOwnerValidationWhenItemExistThenReturnItem() {
        int itemId  = 1;

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
    void findBySearchText() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void createComment() {
    }

    @Test
    void findByItemRequestId() {
    }
}