package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Table(name = "Bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero
    private Integer id;

    @Column(name = "start_date", nullable = false)
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User booker;

    @Column(nullable = false)
    private BookingStatus status;
}
