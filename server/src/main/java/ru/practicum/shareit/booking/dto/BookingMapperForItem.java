package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapperForItem {
    BookingMapperForItem INSTANCE = Mappers.getMapper(BookingMapperForItem.class);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoForItem mapToDto(Booking entity);
}





