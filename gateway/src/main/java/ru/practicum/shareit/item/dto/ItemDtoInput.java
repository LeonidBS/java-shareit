package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoInput {

    @NotBlank(message = "Parameter name is empty",
            groups = ValidationGroups.Create.class)
    private String name;

    @NotBlank(message = "Parameter name is empty",
            groups = ValidationGroups.Create.class)
    private String description;

    @NotNull(message = "Parameter name is NULL",
            groups = ValidationGroups.Create.class)
    private Boolean available;

    private Integer requestId;

}