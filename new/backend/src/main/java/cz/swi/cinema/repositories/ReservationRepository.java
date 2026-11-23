package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Id(Long userId);

    @Query("SELECT r FROM Reservation r JOIN r.seats s WHERE s.id = :seatId")
    List<Reservation> findBySeatId(Long seatId);
}
