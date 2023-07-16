package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Table(name = "items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Parameter name is empty")
    private String name;

    @Column
    @NotBlank(message = "Parameter name is empty")
    @Size(max = 200, message = "length of description is more then 200 symbols")
    private String description;

    @Column
    @NotNull(message = "Parameter name is NULL")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ItemRequest itemRequest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId())
                && name.equals(((Item) o).getName())
                && description.equals(((Item) o).getDescription())
                && available.equals(((Item) o).getAvailable())
                && owner.equals(((Item) o).getOwner())
                && itemRequest.equals(((Item) o).getItemRequest());
    }
}
