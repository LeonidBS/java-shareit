package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    Page<Item> findByOwnerIdOrderById (Integer ownerId, Pageable page);

    @Query("SELECT item " +
            "FROM Item AS item " +
            "LEFT JOIN item.owner " +
            "LEFT JOIN item.itemRequest " +
            "WHERE (LOWER(item.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(item.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND item.available = TRUE")
    Page<Item> findBySearchText(String text, Pageable page);

    @Modifying
    @Query("UPDATE Item item " +
            "SET item.available=false, item.owner=null " +
            "WHERE item.owner.id = ?1 ")
    void updateItemsAsIsNotAvailableByUserId(Integer UserId);
}
