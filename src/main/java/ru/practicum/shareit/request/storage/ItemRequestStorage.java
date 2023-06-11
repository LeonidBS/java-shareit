package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {

    List<ItemRequest> findAll();

    ItemRequest findById(Integer id);

    ItemRequest create(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    ItemRequest delete(Integer id);
}