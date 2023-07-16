package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoForBooking {
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

    private Integer ownerId;

    private Integer requestId;

    @Override
    public String toString() {
        return "{id=" + id + ", name=" + name
                + ", description=" + description
                + ", available=" + available
                + ", ownerId=" + ownerId
                + ", requestId=" + requestId + "}";
    }
}