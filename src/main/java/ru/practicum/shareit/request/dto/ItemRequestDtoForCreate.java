package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDtoForCreate {

    private Integer id;

    private String description;

    private LocalDateTime requestDate;

    private Integer requestorId;

    private String requestorName;

}
