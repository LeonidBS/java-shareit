package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemStorage {

    List<Item> findOwnerAll(Integer ownerId);

    Item findById(Integer id);

    List<Item> findBySearchText(String text);

    Item create(Item item);

    Item update(Item item);

    List<Item> setItemsAsIsNotAvailable(Integer id);
}
