package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItemRequest {

    public ItemRequest(Integer id, String description, LocalDateTime created, User requestor) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.requestor = requestor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(name = "requested")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User requestor;

    @OneToMany(mappedBy = "itemRequest")
    @ToString.Exclude
    private List<Item> items;
}
