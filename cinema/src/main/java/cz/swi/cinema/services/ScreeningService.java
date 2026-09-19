package cz.swi.cinema.services;

import cz.swi.cinema.dto.ScreeningDto;
import cz.swi.cinema.dto.SeatDto;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.enums.SeatStatus;
import cz.swi.cinema.exceptions.ResourceNotFoundException;
import cz.swi.cinema.mappers.ScreeningMapper;
import cz.swi.cinema.mappers.SeatMapper;
import cz.swi.cinema.models.Reservation;
import cz.swi.cinema.models.Seat;
import cz.swi.cinema.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ScreeningMapper screeningMapper;
    private final SeatMapper seatMapper;

    public ScreeningService(ScreeningRepository screeningRepository, SeatRepository seatRepository, ReservationRepository reservationRepository,
                            ScreeningMapper screeningMapper, SeatMapper seatMapper) {
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.screeningMapper = screeningMapper;
        this.seatMapper = seatMapper;
    }

    public List<ScreeningDto> listScreenings() {
        return screeningRepository.findAllByOrderByTimeAsc().stream().map(screeningMapper::toDto).toList();
    }

    public List<ScreeningDto> screeningsForMovie(Long movieId) {
        return screeningRepository.findByMovieIdOrderByTimeAsc(movieId).stream().map(screeningMapper::toDto).toList();
    }

    public ScreeningDto getScreening(Long id) {
        return screeningMapper.toDto(screeningRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Screening not found")));
    }

    public List<SeatDto> seatsForScreening(Long screeningId) {
        Map<Long, SeatStatus> seatStates = new HashMap<>();

        LocalDateTime currentTimeMinusHoldDuration = LocalDateTime.now().minus(ReservationService.HOLD_DURATION);
        List<Reservation> activeReservations = reservationRepository.findActiveByScreeningId(screeningId, currentTimeMinusHoldDuration);

        for (Reservation reservation : activeReservations) {
            SeatStatus seatStatus = reservation.getReservationStatus() == ReservationStatus.RESERVED
                    ? SeatStatus.RESERVED
                    : SeatStatus.CANDIDATE;

            for (Seat seat : reservation.getSeats()) {
                SeatStatus previousStatus = seatStates.get(seat.getId());

                if (previousStatus != SeatStatus.RESERVED) {
                    seatStates.put(seat.getId(), seatStatus);
                }
            }
        }

        List<SeatDto> result = new ArrayList<>();

        for (Seat seat : seatRepository.findByScreeningId(screeningId)) {
            SeatStatus status = seatStates.getOrDefault(seat.getId(), SeatStatus.FREE);

            result.add(seatMapper.toDto(seat, status));
        }

        return result;
    }

    public List<SeatDto> availableSeats(Long screeningId) {
        return seatsForScreening(screeningId)
                .stream()
                .filter(seat -> seat.status() == SeatStatus.FREE).toList();
    }

}
