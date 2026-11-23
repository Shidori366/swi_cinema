package cz.swi.cinema.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationDto {
    private Long id;
    private String contactEmail;
    private LocalDateTime createdAt;
    private Long screeningId;
    private String status;
    private List<Long> seatIds;
}
