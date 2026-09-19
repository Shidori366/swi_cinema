package cz.swi.cinema.services;

import cz.swi.cinema.dto.ReservationDto;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.exceptions.ResourceNotFoundException;
import cz.swi.cinema.mappers.ReservationMapper;
import cz.swi.cinema.models.Reservation;
import cz.swi.cinema.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    public static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    private final ScreeningRepository screenings;
    private final SeatRepository seats;
    private final ReservationRepository reservations;
    private final ReservationMapper reservationMapper;

    public ReservationService(ScreeningRepository screenings, SeatRepository seats, ReservationRepository reservations, ReservationMapper reservationMapper) {
        this.screenings = screenings;
        this.seats = seats;
        this.reservations = reservations;
        this.reservationMapper = reservationMapper;
    }

    @Transactional
    public ReservationDto create(Long screeningId, Long seatId) {
        reservations.acquireWriteLock(); // Before ANY read; lock remains held through commit.

        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

        var screening = screenings.findById(screeningId).orElseThrow(() -> new ResourceNotFoundException("Screening not found"));
        var seat = seats.findById(seatId).orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (!seat.getRoom().getId().equals(screening.getRoom().getId())) {
            throw new IllegalArgumentException("Seat is not in the screening's room");
        }

        if (screening.getTime() == null || !screening.getTime().isAfter(now)) {
            throw new IllegalStateException("Screening has already started");
        }

        deleteExpired(now);

        if (reservations.isSeatUnavailable(screeningId, seatId, now.minus(HOLD_DURATION))) {
            throw new IllegalStateException("Seat is already held or reserved");
        }

        Reservation reservation = new Reservation();

        reservation.setScreening(screening);
        reservation.getSeats().add(seat);
        reservation.setCreatedAt(now);
        reservation.setReservationStatus(ReservationStatus.PENDING);

        return response(reservations.saveAndFlush(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationDto get(Long id) {
        return response(requireReservation(id));
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public ReservationDto confirm(Long id) {
        reservations.acquireWriteLock();

        var reservation = requireReservation(id);

        if (reservation.getReservationStatus() == ReservationStatus.RESERVED) {
            return response(reservation);
        }

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation is not pending");
        }

        if (!reservation.getCreatedAt().plus(HOLD_DURATION).isAfter(LocalDateTime.now())) {
            reservations.delete(reservation);
            throw new IllegalStateException("Reservation expired");
        }

        reservation.setReservationStatus(ReservationStatus.RESERVED);

        return response(reservation);
    }

    @Transactional
    public void cancel(Long id) {
        reservations.acquireWriteLock();
        reservations.findById(id).ifPresent(reservations::delete);
    }

    @Transactional
    public void cancelPending(Long id, LocalDateTime expectedExpiry) {
        reservations.acquireWriteLock();
        reservations
                .findById(id)
                .filter(r -> r.getReservationStatus() == ReservationStatus.PENDING)
                .filter(r -> r.getCreatedAt().plus(HOLD_DURATION).equals(expectedExpiry))
                .ifPresent(reservations::delete);
    }

    @Transactional
    public List<Long> removeExpired() {
        reservations.acquireWriteLock();
        return deleteExpired(LocalDateTime.now());
    }

    private List<Long> deleteExpired(LocalDateTime now) {
        var expired = reservations.findByReservationStatusAndCreatedAtLessThanEqual(ReservationStatus.PENDING, now.minus(HOLD_DURATION));
        var ids = expired.stream().map(Reservation::getId).toList();

        reservations.deleteAll(expired);
        reservations.flush();

        return ids;
    }

    private Reservation requireReservation(Long id) {
        return reservations
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found or expired"));
    }

    private ReservationDto response(Reservation reservation) {
        var expiresAt = reservation.getReservationStatus() == ReservationStatus.PENDING
                ? reservation.getCreatedAt().plus(HOLD_DURATION)
                : null;
        return reservationMapper.toDto(reservation, expiresAt);
    }
}
