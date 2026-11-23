package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomId(Long roomId);
}
