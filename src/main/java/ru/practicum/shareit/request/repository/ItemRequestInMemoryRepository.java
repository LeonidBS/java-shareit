package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class ItemRequestInMemoryRepository implements ItemRequestRepository {
    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();
    private int id = 1;

    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(itemRequests.values());
    }

    @Override
    public ItemRequest findById(Integer id) {

        return itemRequests.get(id);
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        itemRequest.setId(id++);
        itemRequests.put(itemRequest.getId(), itemRequest);

        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {

        itemRequests.put(itemRequest.getId(), itemRequest);

        return itemRequest;
    }

    @Override
    public ItemRequest delete(Integer id) {
        ItemRequest itemRequest = itemRequests.get(id);
        itemRequests.remove(id);

        return itemRequest;
    }
}
