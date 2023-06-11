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
public class UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public UserService(@Qualifier("inMemory") UserStorage userStorage,
                       ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

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

    public User create(UserDto userDto) {

        if (userStorage.isEmailNotUniq(userDto.getEmail()) != null) {
            log.error("Email {} is already exist", userDto.getEmail());
            throw new EntityUniqException(userDto.getEmail() + " is already exist");
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        log.debug("User has been created: {}", user);
        return userStorage.create(user);
    }

    public User update(User user) {

        if (userStorage.findById(user.getId()) == null) {
            log.error("User with ID {} has not been found", user.getId());
            throw new IdNotFoundException("There is no User with ID: " + user.getId());
        }

        Integer userIdWithSameEmail = userStorage.isEmailNotUniq(user.getEmail());
        if (userIdWithSameEmail != null && !userIdWithSameEmail.equals(user.getId())) {
            log.error("Email {} is already exist", user.getEmail());
            throw new EntityUniqException(user.getEmail() + " is already exist");
        }

        userStorage.update(user);
        log.debug("User has been updated: {}", user);

        return user;
    }

    public User updateByPatch(UserDto userDto, Integer userId) {
        User existedUser = userStorage.findById(userId);
        if (existedUser == null) {
            log.error("User with ID {} has not been found", userId);
            throw new IdNotFoundException("There is no User with ID: " + userId);
        }

        Integer userIdWithSameEmail = userStorage.isEmailNotUniq(userDto.getEmail());
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

        return user;
    }

    public User delete(Integer id) {

        findById(id);
        itemStorage.setItemsAsIsNotAvailable(id);

        return userStorage.delete(id);
    }

}
