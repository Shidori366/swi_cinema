package cz.swi.cinema.services;

import cz.swi.cinema.models.Movie;
import cz.swi.cinema.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<String> allMovieNames() {
        return movieRepository.findAll()
                .stream()
                .map(Movie::getName)
                .toList();
    }
}
