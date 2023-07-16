package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDbService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestDbServiceUnitTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserDbService userDbService;

    @InjectMocks
    private ItemRequestDbService itemRequestDbService;
    private static User requestor;
    private static ItemRequest itemRequest;
    private static ItemDto itemDto;
    private static Item item;

    @BeforeAll
    static void setUp() {
        User owner = InstanceFactory.newUser(2, "owner", "owner@user.com");
        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        itemRequest = InstanceFactory.newItemRequest(1, "request", LocalDateTime.now(), requestor);
        itemDto = InstanceFactory.newItemDto(1, "itemDto", "good itemDto", true,
                2, "owner", null, 1);
        item = InstanceFactory.newItem(1, "itemDto", "good itemDto", true,
                owner, itemRequest);
    }

    @Test
    void findOwnWhenRequestorExistThenReturnRequests() {
        int requestorId = 1;
        List<ItemRequest> requests = List.of(itemRequest);
        ItemRequestDto sourceRequest = ItemRequestMapper.INSTANCE.mapToDto(itemRequest);
        sourceRequest.setItems(List.of(itemDto));
        List<ItemRequestDto> sourceDtoList = List.of(sourceRequest);

        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId))
                .thenReturn(requests);
        when(itemRepository.findByItemRequestId(1))
                .thenReturn(List.of(item));
        when(itemMapper.mapListToItemDto(List.of(item))).thenReturn(List.of(itemDto));

        List<ItemRequestDto> targetListDto = itemRequestDbService.findOwn(requestorId);

        assertEquals(sourceDtoList, targetListDto);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository
                , itemRepository, itemMapper);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .findByRequestorIdOrderByCreatedDesc(requestorId);
        inOrder.verify(itemRepository, times(1))
                .findByItemRequestId(1);
        inOrder.verify(itemMapper, times(1))
                .mapListToItemDto(List.of(item));

    }

    @Test
    void findAllExceptOwnWhenRequestorExistThenReturnRequests() {
        int requestorId = 3;
        Pageable pageable1 = PageRequest.of(0, 5);
        Pageable pageable2 = PageRequest.of(1, 5);
        PageRequest pageRequest1 = PageRequest.of(0, 5);
        PageRequest pageRequest2 = PageRequest.of(1, 5);
        List<ItemRequestDto> list1 = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            list1.add(ItemRequestDto.builder()
                    .id(i)
                    .description("request" + i)
                    .created(LocalDateTime.now().plusMinutes(i))
                    .requestorId(1)
                    .requestorName("requestor")
                    .items(new ArrayList<>())
                    .build());
        }

        Page<ItemRequestDto> page1 = new PageImpl<>(list1, pageable1, 0);
        List<ItemRequestDto> list2 = new ArrayList<>();

        for (int j = 6; j < 9; j++) {
            list2.add(ItemRequestDto.builder()
                    .id(j)
                    .description("request" + j)
                    .created(LocalDateTime.now().plusMinutes(j))
                    .requestorId(1)
                    .requestorName("requestor")
                    .items(new ArrayList<>())
                    .build());
        }

        Page<ItemRequestDto> page2 = new PageImpl<>(list2, pageable2, 0);
        List<ItemRequest> listItemRequest1 = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            listItemRequest1.add(ItemRequest.builder()
                    .id(i)
                    .description("request" + i)
                    .created(list1.get(i - 1).getCreated())
                    .requestor(requestor)
                    .build());
        }

        Page<ItemRequest> pageItemRequest1 = new PageImpl<>(listItemRequest1, pageable1, 0);
        List<ItemRequest> listItemRequest2 = new ArrayList<>();

        for (int j = 6; j < 9; j++) {
            listItemRequest2.add(ItemRequest.builder()
                    .id(j)
                    .description("request" + j)
                    .created(list2.get(j - 6).getCreated())
                    .requestor(requestor)
                    .build());
        }

        Page<ItemRequest> pageItemRequest2 = new PageImpl<>(listItemRequest2, pageable2, 0);
        ItemRequestDto targetDto = ItemRequestMapper.INSTANCE.mapToDto(itemRequest);
        targetDto.setItems(List.of(itemDto));

        when(userDbService.findById(requestorId)).thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1))
                .thenReturn(pageItemRequest1);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest2))
                .thenReturn(pageItemRequest2);
        when(itemRepository.findByItemRequestId(any(Integer.class)))
                .thenReturn(new ArrayList<>());
        when(itemMapper.mapListToItemDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        List<ItemRequestDto> retrievedListPage1
                = itemRequestDbService.findAllExceptOwn(requestorId, 0, 5);
        List<ItemRequestDto> retrievedListPage2
                = itemRequestDbService.findAllExceptOwn(requestorId, 5, 5);

        assertEquals(page1.toList(), retrievedListPage1);
        assertEquals(page2.toList(), retrievedListPage2);

        verify(userDbService, times(2))
                .findById(requestorId);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1);
        verify(itemRepository, times(8))
                .findByItemRequestId(any(Integer.class));
        verify(itemMapper, times(8))
                .mapListToItemDto(new ArrayList<>());
    }

    @Test
    void getByIdWhenIdNotExistThenThrowException() {
        int id = 99;
        int userId = 1;

        when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> itemRequestDbService.getById(id, userId));
    }

    @Test
    void createWhenDateCreatedNull() {
        int requestorId = 1;

        ItemRequestDtoInput newItemRequestInput = ItemRequestDtoInput.builder()
                .description("new request")
                .build();

        when(userDbService.findById(requestorId)).thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRequestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ItemRequestDto retrievedDto = itemRequestDbService.create(newItemRequestInput, requestorId);

        assertEquals("new request", retrievedDto.getDescription());
        assertNotNull(retrievedDto.getCreated());
        assertEquals(1, retrievedDto.getRequestorId());
        assertEquals("requestor", retrievedDto.getRequestorName());
        assertNull(retrievedDto.getItems());
    }
}