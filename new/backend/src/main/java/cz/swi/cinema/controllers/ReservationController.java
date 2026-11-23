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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody CreateReservationRequestDto dto) {
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ReservationDto> pay(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.payReservation(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @GetMapping("/user/{userId}")
    public List<ReservationDto> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.getReservationsByUser(userId);
    }
}
