package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Qualifier("dbService")
public class UserDbService implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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
    public UserDto update(User user) {

        if (userRepository.findById(user.getId()).isEmpty()) {
            log.error("User with ID {} has not been found", user.getId());
            throw new IdNotFoundException("There is no User with ID: " + user.getId());
        }


        userRepository.save(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateByPatch(UserDto userDto, Integer userId) {

        Optional<User> existedOptionalUser = userRepository.findById(userId);
        if (existedOptionalUser.isEmpty()) {
            log.error("User with ID {} has not been found", userId);
            throw new IdNotFoundException("There is no User with ID: " + userId);
        }

        User user = User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : existedOptionalUser.get().getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : existedOptionalUser.get().getEmail())
                .build();
        userRepository.save(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        PageRequest page = PageRequest.of(0, 1);

        if (itemRepository.findByOwnerIdOrderById(id, page).toList().size() != 0) {
            itemRepository.updateItemsAsIsNotAvailableByUserId(id);
        }
        userRepository.deleteById(id);

    }
}
