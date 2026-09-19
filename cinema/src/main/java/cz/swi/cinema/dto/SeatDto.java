package cz.swi.cinema.dto;

import cz.swi.cinema.enums.SeatStatus;

public record SeatDto(Long id, SeatStatus status) {
}
