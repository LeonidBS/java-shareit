package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

    private Integer requestId;

    private Integer bookingQuantity;

    @Override
    public String toString() {
            return "(<{id=" + id + ", name=" + name
                    + ", description=" + description
                    + ", available=" + available
                    + ", ownerId=" + ownerId
                    + ", ownerName=" + ownerName
                    + ", requestId=" + requestId
                    + ", bookingQuantity=" + bookingQuantity + "}>)";
    }
}
