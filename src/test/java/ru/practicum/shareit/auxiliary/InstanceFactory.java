package ru.practicum.shareit.auxiliary;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class InstanceFactory {

    public static User newUser(Integer id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static UserDto newUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    public static ItemDto newItemDto(Integer id, String name, String description,
                                      Boolean available, Integer ownerId,
                                      String ownerName, Integer bookingQuantity,
                                      Integer requestId) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .bookingQuantity(bookingQuantity)
                .requestId(requestId)
                .build();
    }

    public static ItemDtoWithComments newItemDtoWithComments(Integer id, String name, String description,
                                                             Boolean available, BookingDtoForItem lastBooking,
                                                             BookingDtoForItem nextBooking,
                                                             List<CommentDtoForItem> comments, Integer ownerId,
                                                             String ownerName, LocalDateTime requestDate,
                                                             Integer bookingQuantity) {
        return ItemDtoWithComments.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .requestDate(requestDate)
                .bookingQuantity(bookingQuantity)
                .build();
    }

    public static Item newItem(Integer id, String name, String description,
                                      Boolean available, User owner,
                                      ItemRequest itemRequest) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemRequest newItemRequest(Integer id, String description,
                                              LocalDateTime created, User requestor) {
        return ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .requestor(requestor)
                .build();
    }

    public static ItemRequestDto newItemRequestDto(Integer id, String description,
                                                   LocalDateTime created, Integer requestorId,
                                                   String requestorName, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(id)
                .description(description)
                .created(created)
                .requestorId(requestorId)
                .requestorName(requestorName)
                .items(items)
                .build();
    }

    public static Booking newBooking(LocalDateTime start, LocalDateTime end,
                                     Item item, User user, BookingStatus status) {
        return Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public static Comment newComment(Integer id, String text, User author,
                                     LocalDateTime created) {
        return  Comment.builder()
                .id(id)
                .text(text)
                .author(author)
                .created(created)
                .build();
    }
}
