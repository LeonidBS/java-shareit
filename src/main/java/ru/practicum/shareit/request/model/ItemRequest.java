package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero
    private Integer id;

    @Column(nullable = false)
    @NotNull(message = "Parameter name is NULL")
    @Size(max = 200, message = "length of description is more then 200 symbols")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @NotNull(message = "Parameter name is NULL")
    private User requestor;

    @Column(name = "request_date")
    @NotNull(message = "Parameter name is NULL")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;

}
