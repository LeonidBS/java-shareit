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
    @Query(value = "SELECT item " +
            "FROM Item AS item " +
            //   "JOIN FETCH item.user " +
            "LEFT JOIN item.itemRequest " +
            "LEFT JOIN item.owner o " +
            "WHERE o.id = ?1 ")
//            "ORDER BY ?#{#pageable}",
//            countQuery = "SELECT COUNT(item) " +
//                    "FROM Item AS item " +
//                    "JOIN FETCH item.user " +
//                    "WHERE item.id = ?1 ",
        //          nativeQuery = true
        //   )
    Page<Item> findAllByUserId(Integer userId, Pageable page);

    @Query(value = "SELECT item " +
            "FROM Item AS item " +
            "LEFT JOIN item.owner " +
            "LEFT JOIN item.itemRequest " +
            "WHERE (LOWER(item.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(item.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND item.available = TRUE")
//            "ORDER BY ?#{#pageable}",
//            countQuery = "SELECT COUNT(*) " +
//                    "FROM Item AS item " +
//                    "JOIN FETCH item.user AS u " +
//                    "WHERE (LOWER(item.name) LIKE LOWER(?1) OR LOWER(item.description) LIKE LOWER(?1)) " +
//                    "AND item.available = TRUE",
//            nativeQuery = true)
    Page<Item> findBySearchText(String text, Pageable page);

    @Modifying
    @Query("UPDATE Item item " +
            "SET item.available=false, item.owner=null " +
            "WHERE item.owner.id = ?1 ")
    void updateItemsAsIsNotAvailableByUserId(Integer UserId);
}
