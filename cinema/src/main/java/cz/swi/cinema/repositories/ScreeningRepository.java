package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    List<Screening> findAllByOrderByTimeAsc();
    List<Screening> findByMovieIdOrderByTimeAsc(Long movieId);
}
