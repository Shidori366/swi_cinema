package cz.swi.cinema.controllers;

import cz.swi.shared.dto.MovieDto;
import cz.swi.cinema.services.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> allMovies() {
        return ResponseEntity.ok(movieService.allMovies());
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> allMovieNames() {
        return ResponseEntity.ok(movieService.allMovieNames());
    }
}
