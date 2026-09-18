package cz.swi.cinema.controllers;

import cz.swi.cinema.dto.ScreeningDto;
import cz.swi.cinema.dto.SeatDto;
import cz.swi.cinema.services.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping
    public ResponseEntity<List<ScreeningDto>> listScreenings(@RequestParam(required = false) Long movieId) {
        return ResponseEntity.ok(movieId == null
                ? screeningService.listScreenings()
                : screeningService.screeningsForMovie(movieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningDto> getScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getScreening(id));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> seatsForScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.seatsForScreening(id));
    }

    @GetMapping("/{id}/seats/available")
    public ResponseEntity<List<SeatDto>> availableSeats(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.availableSeats(id));
    }
}
