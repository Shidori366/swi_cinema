package cz.swi.cinema.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScreeningDto {
    private Long id;
    private LocalDateTime time;
    private String movieName;
    private String roomName;
    private BigDecimal price;
}
