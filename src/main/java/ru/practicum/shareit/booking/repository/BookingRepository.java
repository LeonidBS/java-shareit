package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findByBookerIdOrderByStartDesc(Integer bookerId, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "LEFT JOIN b.booker u " +
            "WHERE (CURRENT_DATE BETWEEN b.start AND b.end) " +
            "AND u.id = ?1 " +
            "ORDER BY b.start DESC")
    Page<Booking> findCurrentByBookerId(Integer bookerId, Pageable page);

    Page<Booking> findByBookerIdAndEndLessThanOrderByEndDesc(Integer bookerId,
                                                             LocalDateTime currentTime,
                                                             Pageable page);

    Page<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Integer bookerId,
                                                                    LocalDateTime currentTime,
                                                                    Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable page);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "LEFT JOIN b.item i " +
            "LEFT JOIN i.owner o " +
            "WHERE (CURRENT_DATE BETWEEN b.start AND b.end) " +
            "AND o.id = ?1 " +
            "ORDER BY b.start DESC")
    Page<Booking> findCurrentByOwnerId(Integer ownerId, Pageable page);

    Page<Booking> findByItemOwnerIdAndEndLessThanOrderByEndDesc(Integer ownerId,
                                                                LocalDateTime currentTime,
                                                                Pageable page);

    Page<Booking> findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(Integer ownerId,
                                                                       LocalDateTime currentTime,
                                                                       Pageable page);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status, Pageable page);

    @Query("SELECT COUNT(*) " +
            "FROM Booking AS b " +
            "LEFT JOIN b.item i " +
            "WHERE b.status = ?1 AND i.id = ?2 ")
    Integer quantityBookingsByStatusAndItemId(BookingStatus status, Integer itemId);

    BookingDtoForItem findFirstBookingByItemIdAndEndLessThanOrderByEndDesc(Integer itemId,
                                                                           LocalDateTime currentTme);

    BookingDtoForItem findFirstBookingByItemIdAndStartGreaterThanOrderByStart(Integer itemId,
                                                                    LocalDateTime currentTme);
}
