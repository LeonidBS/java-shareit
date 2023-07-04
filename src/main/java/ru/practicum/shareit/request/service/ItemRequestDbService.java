package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestDbService implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    @Qualifier("dbService")
    private final ItemService itemService;
    @Qualifier("dbService")
    private final UserService userService;


    @Override
    public List<ItemRequestDto> findAllOwn(Integer requestorId) {

        return itemRequestRepository.
                findByRequestorId(requestorId).stream()
                .map(r -> {
                    ItemRequestDto requestDto = ItemRequestMapper.mapToDto(r);
                    setListItemDto(requestDto);
                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllExceptOwn(Integer requestorId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRequestRepository.
                findByRequestorIdNot(requestorId, page).stream()
                .map(r -> {
                    ItemRequestDto requestDto = ItemRequestMapper.mapToDto(r);
                    setListItemDto(requestDto);
                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Integer id) {
        ItemRequestDto requestDto = ItemRequestMapper
                .mapToDto(itemRequestRepository.findById(id)
                        .orElseThrow(() -> new IdNotFoundException("ItemRequest not found")));
        setListItemDto(requestDto);

        return requestDto;
    }

    @Override
    public ItemRequestDto create(ItemRequestDtoInput dtoInput, Integer requestorId) {
        UserDto userDto = userService.findById(requestorId);

        if (dtoInput.getRequestDate() == null) {
            dtoInput.setRequestDate(LocalDateTime.now());
        }

        @Valid ItemRequest itemRequest = ItemRequest.builder()
                .description(dtoInput.getDescription())
                .requestDate(dtoInput.getRequestDate())
                .requestor(UserMapper.mapToUser(userDto))
                .build();

        itemRequestRepository.save(itemRequest);
        log.debug("ItemRequest has been created: {}", itemRequest);

        return ItemRequestMapper.mapToDto(itemRequest);
    }

    @Override
    public void delete(Integer id) {

        itemRequestRepository.deleteById(id);
    }

    private void setListItemDto(ItemRequestDto requestDto) {

        requestDto.setItems(itemService.findByItemRequestId(requestDto.getId()));
    }

}
