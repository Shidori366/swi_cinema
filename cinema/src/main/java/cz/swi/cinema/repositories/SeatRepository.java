package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomIdOrderByIdAsc(Long roomId);

    @Query("select s from Seat s where s.room.id = (select sc.room.id from Screening sc where sc.id = :screeningId) order by s.id")
    List<Seat> findByScreeningId(Long screeningId);
}
