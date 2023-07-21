package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Qualifier("dbService")
public class UserDbService implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<UserDto> findAll(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return UserMapper.mapListToUserDto(userRepository.findAll(page).toList());
    }

    @Override
    public UserDto findById(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} has not been found", id);
                    return new IdNotFoundException("There is no User with ID: " + id);
                });

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        log.debug("User has been created: {}", user);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {

        User userToUpdate = userRepository.findById(userDto.getId())
                .orElseThrow(() -> {
                    log.error("User with ID {} has not been found", userDto.getId());
                    return new IdNotFoundException("There is no User with ID: " + userDto.getId());
                });
        userToUpdate.setName(userDto.getName());
        userToUpdate.setEmail(userDto.getEmail());

        log.debug("User has been updated: {}", userToUpdate);

        return UserMapper.mapToUserDto(userRepository.save(userToUpdate));
    }

    @Override
    @Transactional
    public UserDto updateByPatch(UserDto userDto, Integer userId) {

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} has not been found", userId);
                    return new IdNotFoundException("There is no User with ID: " + userId);
                });

        if (userDto.getName() != null) userToUpdate.setName(userDto.getName());
        if (userDto.getEmail() != null) userToUpdate.setEmail(userDto.getEmail());

        log.debug("User has been updated: {}", userToUpdate);

        return UserMapper.mapToUserDto(userRepository.save(userToUpdate));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        PageRequest page = PageRequest.of(0, 1);

        if (itemRepository.findByOwnerIdOrderById(id, page).toList().size() != 0) {
            itemRepository.updateItemsAsIsNotAvailableByUserId(id);
        }

        if (bookingRepository.findByBookerIdOrderByStartDesc(id, page).toList().size() != 0) {
            bookingRepository.updateBookingsDeletingByUserId(id);
        }

        if (itemRequestRepository.findByRequestorIdOrderByCreatedDesc(id).size() != 0) {
            itemRequestRepository.updateRequestsByDeletingUserId(id);
        }

        if (commentRepository.findByAuthorId(id).size() != 0) {
            commentRepository.deleteByAuthorId(id);
        }

        userRepository.deleteByUserId(id);
    }
}
