package ru.practicum.shareit.auxiliary;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
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

    public static ItemDtoForBooking newItemDtoForBooking(Integer id, String name, String description,
                                                  Boolean available, Integer ownerId, Integer requestId) {
        return ItemDtoForBooking.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
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

    public static Booking newBooking(Integer id, LocalDateTime start, LocalDateTime end,
                                     Item item, User user, BookingStatus status) {
        return Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public static BookingDtoForItem newBookingDtoForItem(Integer id, LocalDateTime start, LocalDateTime end,
                                     BookingStatus status, Integer bookerId) {
        return BookingDtoForItem.builder()
                .id(id)
                .start(start)
                .end(end)
                .status(status)
                .bookerId(bookerId)
                .build();
    }

    public static Comment newComment(Integer id, String text,
                                     Item item, User author,
                                     LocalDateTime created) {
        return  Comment.builder()
                .id(id)
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    public static CommentDto newCommentDto(Integer id, String text,
                                           Integer itemId, String itemName,
                                           Integer authorId, String authorName,
                                           LocalDateTime created) {
        return  CommentDto.builder()
                .id(id)
                .text(text)
                .itemId(itemId)
                .itemName(itemName)
                .authorId(authorId)
                .authorName(authorName)
                .created(created)
                .build();
    }

    public static CommentDtoForItem newCommentDtoForItem(Integer id, String text,
                                           Integer itemId, Integer authorId, String authorName,
                                           LocalDateTime created) {
        return  CommentDtoForItem.builder()
                .id(id)
                .text(text)
                .itemId(itemId)
                .authorId(authorId)
                .authorName(authorName)
                .created(created)
                .build();
    }
}
