package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.auxiliary.InstanceFactory;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private static ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = InstanceFactory.newItemDto(1, "itemDto", "good itemDto", true,
                2, "owner", null, 1);
    }

    @SneakyThrows
    @Test
    void testItemDtoWhenNameAndEmailCorrect() {
        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("itemDto");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("good itemDto");
    }

    @SneakyThrows
    @Test
    void testItemDtoDeserialization() {
        ObjectMapper mapper = new ObjectMapper();
        ItemDto itemDtoResult = json.parseObject(mapper.writeValueAsString(itemDto));

        assertThat(itemDtoResult).isEqualTo(itemDto);
    }

    @Test
    void testItemToStringWhenToStringResultCorrect() {

        assertThat("(<{id=1, name=itemDto, description=good itemDto, " +
                "available=true, ownerId=2, ownerName=owner, requestId=1, " +
                "bookingQuantity=null}>)")
                .isEqualTo(itemDto.toString());
    }
}