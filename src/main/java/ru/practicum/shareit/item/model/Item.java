package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@Builder
public class Item {
    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Parameter name is empty")
    private String name;

    @NotBlank(message = "Parameter name is empty")
    @Size(max = 200, message = "length of description is more then 200 symbols")
    private String description;

    @NotNull(message = "Parameter name is NULL")
    private Boolean available;

    @PositiveOrZero
    private Integer ownerId;

    private ItemRequest itemRequest;
}
