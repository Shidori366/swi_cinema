package cz.swi.shared.dto;

import java.util.List;

public record CreateReservationRequestDto(Long screeningId, List<Long> seatIds, String email) {
}
