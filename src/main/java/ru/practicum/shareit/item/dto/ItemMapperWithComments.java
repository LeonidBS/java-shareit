package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.comment.dto.CommentMapperForItem;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperWithComments {
    private final BookingDbService bookingService;
    private final CommentRepository commentRepository;

    public ItemDtoWithComments mapToItemDto(Item item, Integer userId) {

        ItemDtoWithComments itemDtoWithComments;
        if (item.getOwner() != null) {
            itemDtoWithComments = new ItemDtoWithComments(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getOwner().getId().equals(userId) ?
                            bookingService.findLastBookingByItemId(item.getId()) : null,
                    item.getOwner().getId().equals(userId) ?
                            bookingService.findNextBookingByItemId(item.getId()) : null,
                    CommentMapperForItem.mapListToDto(commentRepository
                            .findByItemId(item.getId())),
                    item.getOwner().getId(),
                    item.getOwner().getName(),
                    item.getItemRequest() != null ?
                            item.getItemRequest().getRequestDate() : null,
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
                            item.getItemRequest().getRequestDate() : null,
                    bookingService.quantityBookingByStatusAndItemId(BookingStatus.APPROVED, item.getId())
            );
        }

        return itemDtoWithComments;
    }

    public List<ItemDtoWithComments> mapListToItemDto(List<Item> items, Integer userId) {
        List<ItemDtoWithComments> itemDtoWithCommentsList = new ArrayList<>();

        for (Item item : items) {
            itemDtoWithCommentsList.add(mapToItemDto(item, userId));
        }

        return itemDtoWithCommentsList;
    }
}
