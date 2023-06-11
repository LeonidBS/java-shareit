package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface UserStorage {
    List<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(User user);

    User delete(Integer id);

    Integer isEmailNotUniq(String email);
}