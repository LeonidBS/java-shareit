package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.comment.dto.CommentMapperForItem;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperWithComments {
    private final BookingDbService bookingService;
    private final CommentRepository commentRepository;

    public ItemDtoWithComments mapToItemDto(Item item, User owner, ItemRequest request, Integer userId) {

        ItemDtoWithComments itemDtoWithComments;
        if (item.getOwner() != null) {
            itemDtoWithComments = new ItemDtoWithComments(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    owner.getId().equals(userId) ?
                            bookingService.findLastBookingByItemId(item.getId()) : null,
                    owner.getId().equals(userId) ?
                            bookingService.findNextBookingByItemId(item.getId()) : null,
                    CommentMapperForItem.mapListToDto(commentRepository
                            .findByItemId(item.getId())),
                    owner.getId(),
                    owner.getName(),
                    request != null ?
                            request.getCreated() : null,
                    bookingService.quantityBookingByStatusAndItemId(BookingStatus.APPROVED, item.getId())
            );
        } else {
            itemDtoWithComments = new ItemDtoWithComments(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    null,
                    CommentMapperForItem.mapListToDto(commentRepository
                            .findByItemId(item.getId())),
                    null,
                    null,
                    item.getItemRequest() != null ?
                            item.getItemRequest().getCreated() : null,
                    bookingService.quantityBookingByStatusAndItemId(BookingStatus.APPROVED, item.getId())
            );
        }

        return itemDtoWithComments;
    }

    public List<ItemDtoWithComments> mapListToItemDto(List<Item> items, Integer userId) {
        List<ItemDtoWithComments> itemDtoWithCommentsList = new ArrayList<>();

        for (Item item : items) {
            itemDtoWithCommentsList.add(mapToItemDto(item, item.getOwner(),
                    item.getItemRequest(), userId));
        }

        return itemDtoWithCommentsList;
    }
}
