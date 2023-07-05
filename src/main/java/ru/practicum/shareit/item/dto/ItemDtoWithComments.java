package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.comment.dto.CommentDtoForItem;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoWithComments {
    private Integer id;

    @NotBlank(message = "Parameter name is empty",
            groups = ValidationGroups.Create.class)
    private String name;

    @NotBlank(message = "Parameter name is empty",
            groups = ValidationGroups.Create.class)
    @Size(max = 200, message = "length of description is more then 200 symbols",
            groups = ValidationGroups.Create.class)
    private String description;

    @NotNull(message = "Parameter name is NULL",
            groups = ValidationGroups.Create.class)
    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDtoForItem> comments;

    private Integer ownerId;

    private String ownerName;

    private LocalDateTime requestDate;

    private Integer bookingQuantity;
}