package cz.swi.cinema.mappers;

import cz.swi.cinema.dto.MovieDto;
import cz.swi.cinema.models.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieDto toDto(Movie movie);
}
