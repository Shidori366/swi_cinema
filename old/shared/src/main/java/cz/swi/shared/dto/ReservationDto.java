package cz.swi.shared.dto;

import cz.swi.shared.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationDto(Long id, Long screeningId, List<Long> seatIds, ReservationStatus status, LocalDateTime expiresAt, String contactEmail) {
}
