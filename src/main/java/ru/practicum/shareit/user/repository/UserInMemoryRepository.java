package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class UserInMemoryRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public Page<User> findAll(Pageable page) {
        List<User> usersList = new ArrayList<>(users.values());

        return new PageImpl<>(usersList,
                PageRequest.of(page.getPageNumber(), page.getPageSize(), page.getSort()),
                usersList.size());
    }

    public Optional<User> findById(Integer id) {

        return Optional.of(users.get(id));
    }

    public User save(User user) {

        if (user.getId() == null) {
            user.setId(id++);
        }

        users.put(user.getId(), user);

        return user;
    }

    public void deleteById(Integer id) {
        users.remove(id);

    }

    public Integer findUserByEmail(String email) {
        for (User value : users.values()) {
            if (value.getEmail().equals(email)
                    && !email.isEmpty()) {
                return value.getId();
            }
        }
        return null;
    }
}
