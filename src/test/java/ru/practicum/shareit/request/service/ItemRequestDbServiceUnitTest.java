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
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDbService;
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
    private UserDbService userDbService;

    @Mock
    private ItemDbService itemDbService;

    @InjectMocks
    private ItemRequestDbService itemRequestDbService;

    private static User requestor;
    private static User owner;
    private static ItemRequest itemRequest;
    private static Item item;
    private static ItemDto itemDto;

    @BeforeAll
    static void setUp() {
        requestor = User.builder()
                .id(1)
                .name("requestor")
                .email("requestor@user.com")
                .build();

        owner = User.builder()
                .id(2)
                .name("owner")
                .email("owner@user.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("request")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        item = Item.builder()
                .id(1)
                .name("item")
                .description("good item")
                .available(true)
                .owner(owner)
                .itemRequest(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("itemDto")
                .description("good itemDto")
                .available(true)
                .ownerId(2)
                .ownerName("owner")
                .bookingQuantity(null)
                .requestId(1)
                .build();
    }

    @Test
    void findOwnWhenRequestorExist() {
        int requestorId = 1;
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemDto> itemsDto = List.of(itemDto);
        ItemRequestDto expectedRequest = ItemRequestMapper.INSTANCE.mapToDto(itemRequest);
        expectedRequest.setItems(List.of(itemDto));
        List<ItemRequestDto> expectedRequests = List.of(expectedRequest);

        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId))
                .thenReturn(requests);
        when(itemDbService.findByItemRequestId(1))
                .thenReturn(itemsDto);

        List<ItemRequestDto> requestsDto = itemRequestDbService.findOwn(requestorId);

        assertEquals(requestsDto, expectedRequests);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository, itemDbService);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .findByRequestorIdOrderByCreatedDesc(requestorId);
        inOrder.verify(itemDbService, times(1))
                .findByItemRequestId(requestorId);

    }

    @Test
    void findAllExceptOwnWhenRequestorExist() {
        int requestorId = 3;
        Pageable pageable1 = PageRequest.of(0, 5);
        Pageable pageable2 = PageRequest.of(1, 5);
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
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemDto> itemsDto = List.of(itemDto);
        ItemRequestDto expectedRequest = ItemRequestMapper.INSTANCE.mapToDto(itemRequest);
        expectedRequest.setItems(List.of(itemDto));
        List<ItemRequestDto> expectedRequests = List.of(expectedRequest);

        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageable1))
                .thenReturn(pageItemRequest1);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageable2))
                .thenReturn(pageItemRequest2);
        when(itemDbService.findByItemRequestId(anyInt()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> retrievedListPage1 = itemRequestDbService.findAllExceptOwn(requestorId, 0, 5);
        List<ItemRequestDto> retrievedListPage2 = itemRequestDbService.findAllExceptOwn(requestorId, 5, 5);

        assertEquals(page1.toList(), retrievedListPage1);
        assertEquals(page2.toList(), retrievedListPage2);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository, itemDbService);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageable1);
        inOrder.verify(itemDbService, times(5))
                .findByItemRequestId(anyInt());
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageable2);
        inOrder.verify(itemDbService, times(3))
                .findByItemRequestId(anyInt());
    }

    @Test
    void getByIdWhenIdNotExist() {
        int id = 99;
        int userId = 1;

        when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> itemRequestDbService.getById(id, userId));
    }

    @Test
    void createWhenPassedCreatedNull() {
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
        assertEquals(new ArrayList<>(), retrievedDto.getItems());
    }

    @Test
    void delete() {
    }
}