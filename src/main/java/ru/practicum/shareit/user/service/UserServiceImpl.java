package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityUniqException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("inMemory") UserStorage userStorage,
                           ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.listToUserDto(userStorage.findAll());
    }

    public UserDto findById(Integer id) {
        User user = userStorage.findById(id);

        if (user == null) {
            log.error("User with ID {} has not been found", id);
            throw new IdNotFoundException("There is no User with ID: " + id);
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {

        if (userStorage.findUserByEmail(userDto.getEmail()) != null) {
            log.error("Email {} is already exist", userDto.getEmail());
            throw new EntityUniqException(userDto.getEmail() + " is already exist");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        log.debug("User has been created: {}", user);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(User user) {

        if (userStorage.findById(user.getId()) == null) {
            log.error("User with ID {} has not been found", user.getId());
            throw new IdNotFoundException("There is no User with ID: " + user.getId());
        }

        Integer userIdWithSameEmail = userStorage.findUserByEmail(user.getEmail());
        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(user.getId())) {
            log.error("Email {} is already exist", user.getEmail());
            throw new EntityUniqException(user.getEmail() + " is already exist");
        }

        userStorage.update(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateByPatch(UserDto userDto, Integer userId) {
        User existedUser = userStorage.findById(userId);
        if (existedUser == null) {
            log.error("User with ID {} has not been found", userId);
            throw new IdNotFoundException("There is no User with ID: " + userId);
        }

        Integer userIdWithSameEmail = userStorage.findUserByEmail(userDto.getEmail());
        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(userId)) {
            log.error("Email {} is already exist", userDto.getEmail());
            throw new EntityUniqException(userDto.getEmail() + " is already exist");
        }

        User user = User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : existedUser.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : existedUser.getEmail())
                .build();

        userStorage.update(user);
        log.debug("User has been updated: {}", user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto delete(Integer id) {

        findById(id);
        itemStorage.setItemsAsIsNotAvailable(id);

        return UserMapper.toUserDto(userStorage.delete(id));
    }
}
