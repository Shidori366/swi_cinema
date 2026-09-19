package cz.swi.cinema.dto;

import cz.swi.cinema.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationDto(Long id, Long screeningId, List<Long> seatIds, ReservationStatus status, LocalDateTime expiresAt) {
}
