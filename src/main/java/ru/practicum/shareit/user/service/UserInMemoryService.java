package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityUniqException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.repository.ItemInMemoryRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserInMemoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Qualifier("inMemoryService")
@RequiredArgsConstructor
public class UserInMemoryService implements UserService {
    private final UserInMemoryRepository userInMemoryRepository;
    private final ItemInMemoryRepository itemInMemoryRepository;

    @Override
    public List<UserDto> findAll(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return UserMapper.mapListToUserDto(userInMemoryRepository.findAll(page).toList());
    }

    @Override
    public UserDto findById(Integer id) {
        Optional<User> optionalUser = userInMemoryRepository.findById(id);

        if (optionalUser.isEmpty()) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return UserMapper.mapToUserDto(optionalUser.get());
    }

    @Override
    public UserDto create(UserDto userDto) {

        if (userInMemoryRepository.findUserByEmail(userDto.getEmail()) != null) {
            log.error("Email {} is already exist", userDto.getEmail());
            throw new EntityUniqException(userDto.getEmail() + " is already exist");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        log.debug("User has been created: {}", user);
        return UserMapper.mapToUserDto(userInMemoryRepository.save(user));
    }

    @Override
    public UserDto update(UserDto useDto) {
        User user = UserMapper.mapToUser(useDto);
        if (userInMemoryRepository.findById(user.getId()).isEmpty()) {
            log.error("User with ID {} has not been found", user.getId());
            throw new IdNotFoundException("There is no User with ID: " + user.getId());
        }

        Integer userIdWithSameEmail = userInMemoryRepository.findUserByEmail(user.getEmail());
        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(user.getId())) {
            log.error("Email {} is already exist", user.getEmail());
            throw new EntityUniqException(user.getEmail() + " is already exist");
        }

        userInMemoryRepository.save(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateByPatch(UserDto userDto, Integer userId) {
        Optional<User> existedOptionalUser = userInMemoryRepository.findById(userId);
        if (existedOptionalUser.isEmpty()) {
            log.error("User with ID {} has not been found", userId);
            throw new IdNotFoundException("There is no User with ID: " + userId);
        }

        Integer userIdWithSameEmail = userInMemoryRepository.findUserByEmail(userDto.getEmail());
        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(userId)) {
            log.error("Email {} is already exist", userDto.getEmail());
            throw new EntityUniqException(userDto.getEmail() + " is already exist");
        }

        User user = User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : existedOptionalUser.get().getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : existedOptionalUser.get().getEmail())
                .build();

        userInMemoryRepository.save(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteById(Integer id) {
        findById(id);
        userInMemoryRepository.deleteById(id);
        itemInMemoryRepository.setItemsAsIsNotAvailable(id);
    }
}
