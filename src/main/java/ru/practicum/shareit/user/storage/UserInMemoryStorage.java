package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer id) {

        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User delete(Integer id) {
        User user = users.get(id);
        users.remove(id);

        return user;
    }

    @Override
    public Integer isEmailNotUniq(String email) {
        for (User value : users.values()) {
            if (value.getEmail().equals(email)
                    && !email.isEmpty()) {
                return value.getId();
            }
        }
        return null;
    }

}
