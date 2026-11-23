package cz.swi.cinema.dto;

import cz.swi.cinema.models.Seat;
import lombok.Data;

@Data
public class SeatDto {
    private Long id;
    private String status;
    private String seatNumber;
}
