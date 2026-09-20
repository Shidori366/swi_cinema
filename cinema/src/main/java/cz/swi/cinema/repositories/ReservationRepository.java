package cz.swi.cinema.repositories;

import cz.swi.shared.enums.ReservationStatus;
import cz.swi.cinema.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByScreeningId(Long screeningId);

    List<Reservation> findByContactEmail(String contactEmail);

    @Query("""
            select distinct r from Reservation r left join fetch r.seats
            where r.screening.id = :screeningId and
            (r.reservationStatus = cz.swi.shared.enums.ReservationStatus.RESERVED or
            (r.reservationStatus = cz.swi.shared.enums.ReservationStatus.PENDING and r.createdAt > :cutoff))
            """)
    List<Reservation> findActiveByScreeningId(Long screeningId, LocalDateTime cutoff);

    @Query("""
            select count(r) > 0 from Reservation r join r.seats s
            where r.screening.id = :screeningId and s.id in (:seatIds) and
            (r.reservationStatus = cz.swi.shared.enums.ReservationStatus.RESERVED or
            (r.reservationStatus = cz.swi.shared.enums.ReservationStatus.PENDING and r.createdAt > :cutoff))
            """)
    boolean isAnySeatUnavailable(Long screeningId, List<Long> seatIds, LocalDateTime cutoff);

    List<Reservation> findByReservationStatusAndCreatedAtLessThanEqual(ReservationStatus status, LocalDateTime cutoff);

    /**
     * SQLite has a single writer and no SELECT FOR UPDATE support. This must be
     * the FIRST database statement of every reservation-changing transaction.
     * The no-op UPDATE acquires its database write lock until commit/rollback,
     * including when no screening exists. Other writers wait before reading.
     */
    @Modifying
    @Query(value = "update screening set id = id where id = (select min(id) from screening)", nativeQuery = true)
    void acquireWriteLock();
}
