package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestDbService implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    @Qualifier("dbService")
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public List<ItemRequestDto> findOwn(Integer requestorId) {
        userService.findById(requestorId);

        return itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(itemRequestMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllExceptOwn(Integer requestorId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        userService.findById(requestorId);

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, page)
                .stream()
                .map(itemRequestMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(Integer requestId, Integer userId) {
        userService.findById(userId);
        return itemRequestMapper
                .mapToDto(itemRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new IdNotFoundException("ItemRequest not found")));
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDtoInput dtoInput, Integer requestorId) {
        UserDto userDto = userService.findById(requestorId);

        if (dtoInput.getRequestDate() == null) {
            dtoInput.setRequestDate(LocalDateTime.now());
        }

        ItemRequest itemRequest = ItemRequest.builder()
                .description(dtoInput.getDescription())
                .created(dtoInput.getRequestDate())
                .requestor(UserMapper.mapToUser(userDto))
                .build();

        log.debug("ItemRequest has been created: {}", itemRequest);

        return itemRequestMapper.mapToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public void delete(Integer id) {

        itemRequestRepository.deleteById(id);
    }
}
