package cz.swi.shared.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScreeningDto(Long id, LocalDateTime time, MovieDto movie, Long roomId, BigDecimal price) {
}
