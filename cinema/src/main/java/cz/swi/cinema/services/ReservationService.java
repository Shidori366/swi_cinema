package cz.swi.cinema.services;

import cz.swi.cinema.dto.ReservationDto;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.exceptions.ResourceNotFoundException;
import cz.swi.cinema.mappers.ReservationMapper;
import cz.swi.cinema.models.Reservation;
import cz.swi.cinema.models.Screening;
import cz.swi.cinema.models.Seat;
import cz.swi.cinema.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    public static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(ScreeningRepository screeningRepository, SeatRepository seatRepository, ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    @Transactional
    public ReservationDto create(Long screeningId, List<Long> seatIds) {
        if (seatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats to reserve");
        }
        reservationRepository.acquireWriteLock(); // Before ANY read; lock remains held through commit.

        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);

        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new ResourceNotFoundException("Screening not found"));
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("Seat not found");
        }

        if (seats.stream().anyMatch(s -> !s.getRoom().getId().equals(screening.getRoom().getId()))) {
            throw new IllegalArgumentException("Seat is not in the screening's room");
        }

        if (screening.getTime() == null || !screening.getTime().isAfter(now)) {
            throw new IllegalStateException("Screening has already started");
        }

        deleteExpired(now);

        if (reservationRepository.isAnySeatUnavailable(screeningId, seatIds, now.minus(HOLD_DURATION))) {
            throw new IllegalStateException("Seat is already held or reserved");
        }

        Reservation reservation = new Reservation();

        reservation.setScreening(screening);
        reservation.getSeats().addAll(seats);
        reservation.setCreatedAt(now);
        reservation.setReservationStatus(ReservationStatus.PENDING);

        return response(reservationRepository.saveAndFlush(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationDto get(Long id) {
        return response(requireReservation(id));
    }

    @Transactional(noRollbackFor = IllegalStateException.class)
    public ReservationDto confirm(Long id) {
        reservationRepository.acquireWriteLock();

        Reservation reservation = requireReservation(id);

        if (reservation.getReservationStatus() == ReservationStatus.RESERVED) {
            return response(reservation);
        }

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation is not pending");
        }

        if (!reservation.getCreatedAt().plus(HOLD_DURATION).isAfter(LocalDateTime.now())) {
            reservationRepository.delete(reservation);
            throw new IllegalStateException("Reservation expired");
        }

        reservation.setReservationStatus(ReservationStatus.RESERVED);

        return response(reservation);
    }

    @Transactional
    public void cancel(Long id) {
        reservationRepository.acquireWriteLock();
        reservationRepository.findById(id).ifPresent(reservationRepository::delete);
    }

    private void deleteExpired(LocalDateTime now) {
        List<Reservation> expired = reservationRepository.findByReservationStatusAndCreatedAtLessThanEqual(ReservationStatus.PENDING, now.minus(HOLD_DURATION));

        reservationRepository.deleteAll(expired);
        reservationRepository.flush();
    }

    private Reservation requireReservation(Long id) {
        return reservationRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found or expired"));
    }

    private ReservationDto response(Reservation reservation) {
        LocalDateTime expiresAt = reservation.getReservationStatus() == ReservationStatus.PENDING
                ? reservation.getCreatedAt().plus(HOLD_DURATION)
                : null;
        return reservationMapper.toDto(reservation, expiresAt);
    }
}
