package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "requestorId", source = "requestor.id")
    @Mapping(target = "requestorName", source = "requestor.name")
    @Mapping(target = "items", ignore = true)
    ItemRequestDto mapToDto(ItemRequest entity);
}


