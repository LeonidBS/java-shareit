package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;


    @Override
    public List<Item> findOwnerAll(Integer ownerId) {

        return items.values().stream()
                .filter(i -> (i.getOwnerId().equals(ownerId)))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Integer id) {
        return items.get(id);
    }

    @Override
    public List<Item> findBySearchText(String text) {

        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains((text.toLowerCase()))))
                .filter(i -> i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> setItemsAsIsNotAvailable(Integer ownerId) {
        List<Item> notActiveItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                item.setAvailable(false);
                notActiveItems.add(item);
            }
        }
        return notActiveItems;
    }
}
