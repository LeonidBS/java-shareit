package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoForItemTest {
    @Autowired
    private JacksonTester<BookingDtoForItem> json;

    @SneakyThrows
    @Test
    void testBookingDtoSerialization() {
        User requestor = InstanceFactory.newUser(1, "requstor",
                "requstor@user.com");
        DateTimeFormatter dateTimeFormat = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime futureStartDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        LocalDateTime futureEndDateTime = LocalDateTime.parse(LocalDateTime.now().plusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        BookingDtoForItem bookingDtoForItem = InstanceFactory.newBookingDtoForItem(1,
                futureStartDateTime, futureEndDateTime,
                BookingStatus.APPROVED, requestor.getId());

        JsonContent<BookingDtoForItem> result = json.write(bookingDtoForItem);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dateTimeFormat.format(futureStartDateTime));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dateTimeFormat.format(futureEndDateTime));
    }
}