package cz.swi.cinema.dto;

import cz.swi.cinema.models.Movie;
import lombok.Data;

@Data
public class MovieDto {
    private Long id;
    private String name;
    private String director;
    private Long duration;
}
