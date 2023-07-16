package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.auxiliary.InstanceFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void testItemDtoSerialization() {
        User requestor = InstanceFactory.newUser(1,
                "requestor", "requestor@user.com");
        ItemDto itemDto = InstanceFactory.newItemDto(1,
                "itemDto", "good itemDto", true,
                2, "owner", null, 1);
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("" +
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = InstanceFactory.newItemRequestDto(1,
                "request", created, 1,
                requestor.getName(), List.of(itemDto));

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("request");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(
                dateTimeFormat.format(created));
    }

}