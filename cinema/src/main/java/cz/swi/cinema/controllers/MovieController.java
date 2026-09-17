package cz.swi.cinema.controllers;

import cz.swi.cinema.services.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> allMovieNames() {
        return ResponseEntity.ok(movieService.allMovieNames());
    }
}
