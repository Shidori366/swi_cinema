package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    List<Screening> findByTimeBetween(LocalDateTime start, LocalDateTime end);
}
