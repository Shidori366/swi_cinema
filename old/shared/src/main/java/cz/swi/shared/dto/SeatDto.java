package cz.swi.shared.dto;

import cz.swi.shared.enums.SeatStatus;

public record SeatDto(Long id, SeatStatus status) {
}
