package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingDbService;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class ItemMapperWithComments {
    private final BookingDbService bookingService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

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
                    commentMapper.mapListToDto(commentRepository
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
                    commentMapper.mapListToDto(commentRepository
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
}
