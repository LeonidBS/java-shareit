package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestDbServiceUnitTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserDbService userDbService;

    @InjectMocks
    private ItemRequestDbService itemRequestDbService;
    private User requestor;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now();
        requestor = InstanceFactory.newUser(1, "requestor", "requestor@user.com");
        itemRequest = InstanceFactory.newItemRequest(1, "request", created, requestor);
        ItemDto itemDto = InstanceFactory.newItemDto(1, "itemDto", "good itemDto", true,
                2, "owner", null, 1);
        itemRequestDto = InstanceFactory.newItemRequestDto(1, "request", created, requestor.getId(),
                requestor.getName(), List.of(itemDto));
    }

    @Test
    void findOwnWhenRequestorExistThenReturnRequests() {
        int requestorId = 1;
        List<ItemRequest> requests = List.of(itemRequest);
        List<ItemRequestDto> sourceDtoList = List.of(itemRequestDto);

        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId))
                .thenReturn(requests);
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> targetListDto = itemRequestDbService.findOwn(requestorId);

        assertEquals(sourceDtoList, targetListDto);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository,
                itemRequestMapper);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .findByRequestorIdOrderByCreatedDesc(requestorId);
        inOrder.verify(itemRequestMapper, times(1))
                .mapToDto(itemRequest);
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

        when(userDbService.findById(requestorId)).thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1))
                .thenReturn(pageItemRequest1);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest2))
                .thenReturn(pageItemRequest2);
        when(itemRequestMapper.mapToDto(listItemRequest1.get(0))).thenReturn(list1.get(0));
        when(itemRequestMapper.mapToDto(listItemRequest1.get(1))).thenReturn(list1.get(1));
        when(itemRequestMapper.mapToDto(listItemRequest1.get(2))).thenReturn(list1.get(2));
        when(itemRequestMapper.mapToDto(listItemRequest1.get(3))).thenReturn(list1.get(3));
        when(itemRequestMapper.mapToDto(listItemRequest1.get(4))).thenReturn(list1.get(4));
        when(itemRequestMapper.mapToDto(listItemRequest2.get(0))).thenReturn(list2.get(0));
        when(itemRequestMapper.mapToDto(listItemRequest2.get(1))).thenReturn(list2.get(1));
        when(itemRequestMapper.mapToDto(listItemRequest2.get(2))).thenReturn(list2.get(2));

        List<ItemRequestDto> targetListPage1
                = itemRequestDbService.findAllExceptOwn(requestorId, 0, 5);
        List<ItemRequestDto> targetListPage2
                = itemRequestDbService.findAllExceptOwn(requestorId, 5, 5);

        assertEquals(page1.toList(), targetListPage1);
        assertEquals(page2.toList(), targetListPage2);

        verify(userDbService, times(2))
                .findById(requestorId);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(requestorId, pageRequest1);
        verify(itemRequestMapper, times(8))
                .mapToDto(any());
    }

    @Test
    void getByIdWhenIdNotExistThenThrowException() {
        int id = 99;
        int userId = 1;

        when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> itemRequestDbService.findById(id, userId));
    }

    @Test
    void createWhenDateCreatedNull() {
        int requestorId = 1;

        itemRequestDto.setItems(null);
        ItemRequestDtoInput newItemRequestInput = ItemRequestDtoInput.builder()
                .description(itemRequest.getDescription())
                .requestDate(itemRequest.getCreated())
                .build();

        when(userDbService.findById(requestorId))
                .thenReturn(UserMapper.mapToUserDto(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(itemRequestMapper.mapToDto(any(ItemRequest.class)))
                .thenReturn(itemRequestDto);

        ItemRequestDto targetDto = itemRequestDbService.create(newItemRequestInput, requestorId);

        assertEquals(itemRequestDto, targetDto);
        InOrder inOrder = inOrder(userDbService, itemRequestRepository,
                itemRequestMapper);
        inOrder.verify(userDbService, times(1))
                .findById(requestorId);
        inOrder.verify(itemRequestRepository, times(1))
                .save(any(ItemRequest.class));
        inOrder.verify(itemRequestMapper, times(1))
                .mapToDto(any(ItemRequest.class));
    }
}