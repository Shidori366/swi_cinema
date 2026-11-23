package cz.swi.cinema.services;

import cz.swi.shared.dto.MovieDto;
import cz.swi.cinema.mappers.MovieMapper;
import cz.swi.cinema.models.Movie;
import cz.swi.cinema.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public List<String> allMovieNames() {
        return movieRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(Movie::getName)
                .toList();
    }

    public List<MovieDto> allMovies() {
        return movieRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(movieMapper::toDto)
                .toList();
    }
}
