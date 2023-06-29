package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findByBookerIdOrderByStartDesc(Integer bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
            Integer bookerId, LocalDateTime currentTime1,
            LocalDateTime currentTime2, Pageable page);

    Page<Booking> findByBookerIdAndEndLessThanOrderByEndDesc(
            Integer bookerId, LocalDateTime currentTime, Pageable page);

    Page<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(
            Integer bookerId, LocalDateTime currentTime, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Integer bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(
            Integer ownerId, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartLessThanAndEndGreaterThanOrderByEndDesc(
            Integer ownerId, LocalDateTime currentTime1,
            LocalDateTime currentTime2, Pageable page);

    Page<Booking> findByItemOwnerIdAndEndLessThanOrderByEndDesc(
            Integer ownerId, LocalDateTime currentTime, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(
            Integer ownerId, LocalDateTime currentTime, Pageable page);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Integer ownerId, BookingStatus status, Pageable page);

    Integer countByStatusAndItemId(BookingStatus status, Integer itemId);

    BookingDtoForItem findFirstBookingByItemIdAndStatusAndEndLessThanOrderByEndDesc(
            Integer itemId, BookingStatus status, LocalDateTime currentTme);

    BookingDtoForItem findFirstBookingByItemIdAndStatusAndStartGreaterThanOrderByStart(
            Integer itemId, BookingStatus status, LocalDateTime currentTme);

    Integer countByBookerIdAndItemIdAndStatusAndEndLessThan(
            Integer userId, Integer itemId, BookingStatus status, LocalDateTime currentTime);
}
