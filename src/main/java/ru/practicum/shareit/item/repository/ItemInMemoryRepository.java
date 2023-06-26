package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemory")
@RequiredArgsConstructor
public class ItemInMemoryRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;


    public Page<Item> findAllByUserId(Integer ownerId, Pageable page) {

        List<Item> itemsList = items.values().stream()
                .filter(i -> (i.getOwner().getId().equals(ownerId)))
                .collect(Collectors.toList());

        return new PageImpl<>(itemsList,
                PageRequest.of(page.getPageNumber(), page.getPageSize(), page.getSort()),
                itemsList.size());
    }

    public Optional<Item> findById(Integer id) {
        return Optional.of(items.get(id));
    }

    public Page<Item> findBySearchText(String text, Pageable page) {

        List<Item> itemsList = items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains((text.toLowerCase()))))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());

        return new PageImpl<>(itemsList,
                PageRequest.of(page.getPageNumber(), page.getPageSize(), page.getSort()),
                itemsList.size());
    }

    public Item save(Item item) {

        if (item.getId() == null) {
            item.setId(id++);
        }

        items.put(item.getId(), item);

        return item;
    }

    public List<Item> setItemsAsIsNotAvailable(Integer ownerId) {
        List<Item> notActiveItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(ownerId)) {
                item.setAvailable(false);
                notActiveItems.add(item);
            }
        }
        return notActiveItems;
    }
}
