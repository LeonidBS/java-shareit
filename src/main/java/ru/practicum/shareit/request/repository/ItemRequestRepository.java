package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Integer requestorId);

    Page<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Integer requestorId, Pageable page);


    @Modifying
    @Query("UPDATE ItemRequest ir " +
            "SET ir.requestor=null " +
            "WHERE ir.requestor.id = ?1 ")
    void updateRequestsByDeletingUserId(Integer userId);
}