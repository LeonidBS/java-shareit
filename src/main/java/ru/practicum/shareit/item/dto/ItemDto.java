package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer ownerId;

    private String ownerName;

    private LocalDate requestDate;

    private Integer bookingQuantity;
}
