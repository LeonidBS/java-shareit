package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Integer id;

   private String description;

    private LocalDateTime requestDate;

    private Integer requestorId;

    private String requestorName;

    List<ItemDto>  items;
}
