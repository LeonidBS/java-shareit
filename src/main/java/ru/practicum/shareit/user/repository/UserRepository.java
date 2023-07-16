package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User u WHERE u.id = ?1")
    void deleteByUserId(Integer id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User CASCADE")
    void deleteAllUser();
}