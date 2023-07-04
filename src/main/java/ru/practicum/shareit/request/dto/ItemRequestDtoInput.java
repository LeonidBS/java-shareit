package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDtoInput {

    @NotNull(message = "Parameter name is NULL",
            groups = ValidationGroups.Create.class)
    @Size(min = 1, message = "Request description is empty",
            groups = ValidationGroups.Create.class)
    private String description;

    @FutureOrPresent
    private LocalDateTime requestDate;

}
