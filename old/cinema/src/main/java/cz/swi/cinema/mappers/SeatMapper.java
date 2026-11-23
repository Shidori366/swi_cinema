package cz.swi.cinema.mappers;

import cz.swi.shared.dto.SeatDto;
import cz.swi.shared.enums.SeatStatus;
import cz.swi.cinema.models.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(target = "id", source = "seat.id")
    @Mapping(target = "status", source = "status")
    SeatDto toDto(Seat seat, SeatStatus status);
}
