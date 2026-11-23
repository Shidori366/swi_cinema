package cz.swi.cinema.controllers;

import cz.swi.cinema.dto.*;
import cz.swi.cinema.models.*;
import cz.swi.cinema.repositories.*;
import cz.swi.cinema.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @GetMapping
    public List<ScreeningDto> getAllScreenings() {
        return screeningService.getAllScreenings();
    }

    @GetMapping("/{id}")
    public ScreeningDto getScreeningById(@PathVariable Long id) {
        return screeningService.getScreeningById(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatDto> getSeats(@PathVariable Long id) {
        return screeningService.getSeatsForScreening(id);
    }

    @GetMapping("/{id}/seats/available")
    public List<SeatDto> getAvailableSeats(@PathVariable Long id) {
        return screeningService.getAvailableSeatsForScreening(id);
    }
}
