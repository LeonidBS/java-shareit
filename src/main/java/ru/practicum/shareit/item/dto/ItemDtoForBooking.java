package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoForBooking {
    private Integer id;

    private String name;

    private String description;

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