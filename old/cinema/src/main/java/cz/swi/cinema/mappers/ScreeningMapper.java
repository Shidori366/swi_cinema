package cz.swi.cinema.mappers;

import cz.swi.shared.dto.ScreeningDto;
import cz.swi.cinema.models.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {
    @Mapping(target = "movie", source = "movie")
    @Mapping(target = "roomId", source = "room.id")
    ScreeningDto toDto(Screening screening);
}
