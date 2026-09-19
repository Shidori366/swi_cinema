package cz.swi.cinema.controllers;

import cz.swi.shared.dto.CreateReservationRequestDto;
import cz.swi.shared.dto.ReservationDto;
import cz.swi.cinema.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // TODO: Setup security context for user auth and get email from there, if there is any user currently logged in.
    @PostMapping
    public ResponseEntity<ReservationDto> create(@RequestBody CreateReservationRequestDto request) {
        if (request.screeningId() == null || request.seatIds() == null) {
            throw new IllegalArgumentException("screeningId and seatId must be positive numbers");
        }

        String email = request.email();

        if (email == null) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        ReservationDto reservation = reservationService.create(request.screeningId(), request.seatIds(), email);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.id())
                .toUri();

        return ResponseEntity.created(location).body(reservation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirm(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
