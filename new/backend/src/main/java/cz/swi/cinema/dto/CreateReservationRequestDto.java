package cz.swi.cinema.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateReservationRequestDto {
    private Long screeningId;
    private List<Long> seatIds;
    private Long userId;
}
