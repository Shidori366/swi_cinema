package cz.swi.cinema.controllers;

import cz.swi.cinema.dto.CreateReservationRequestDto;
import cz.swi.cinema.dto.ReservationDto;
import cz.swi.cinema.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDto> create(@RequestBody CreateReservationRequestDto request) {
        if (request.screeningId() == null || request.screeningId() <= 0  || request.seatId() == null || request.seatId() <= 0) {
            throw new IllegalArgumentException("screeningId and seatId must be positive numbers");
        }

        var reservation = reservationService.create(request.screeningId(), request.seatId());
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
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
