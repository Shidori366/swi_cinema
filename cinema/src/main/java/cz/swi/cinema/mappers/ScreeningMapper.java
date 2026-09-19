package cz.swi.cinema.mappers;

import cz.swi.cinema.dto.ScreeningDto;
import cz.swi.cinema.models.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {
    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "roomId", source = "room.id")
    ScreeningDto toDto(Screening screening);
}
