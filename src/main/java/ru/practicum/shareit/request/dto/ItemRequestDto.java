package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    @PositiveOrZero
    private Integer id;

    @Size(max = 200, message = "length of description is more then 200 symbols")
    private String description;

    @NotBlank
    @Size(max = 200, message = "length of description is more then 200 symbols")
    private String requestorName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
}
