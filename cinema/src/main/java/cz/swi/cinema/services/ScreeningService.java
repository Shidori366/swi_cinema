package cz.swi.cinema.services;

import cz.swi.cinema.dto.ScreeningDto;
import cz.swi.cinema.dto.SeatDto;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.enums.SeatStatus;
import cz.swi.cinema.exceptions.ResourceNotFoundException;
import cz.swi.cinema.mappers.ScreeningMapper;
import cz.swi.cinema.mappers.SeatMapper;
import cz.swi.cinema.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScreeningService {

    private final ScreeningRepository screenings;
    private final SeatRepository seats;
    private final ReservationRepository reservations;
    private final ScreeningMapper screeningMapper;
    private final SeatMapper seatMapper;

    public ScreeningService(ScreeningRepository screenings, SeatRepository seats, ReservationRepository reservations,
                            ScreeningMapper screeningMapper, SeatMapper seatMapper) {
        this.screenings = screenings;
        this.seats = seats;
        this.reservations = reservations;
        this.screeningMapper = screeningMapper;
        this.seatMapper = seatMapper;
    }

    public List<ScreeningDto> listScreenings() {
        return screenings.findAllByOrderByTimeAsc().stream().map(screeningMapper::toDto).toList();
    }

    public List<ScreeningDto> screeningsForMovie(Long movieId) {
        return screenings.findByMovieIdOrderByTimeAsc(movieId).stream().map(screeningMapper::toDto).toList();
    }

    public ScreeningDto getScreening(Long id) {
        return screeningMapper.toDto(screenings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Screening not found")));
    }

    public List<SeatDto> seatsForScreening(Long screeningId) {
        getScreening(screeningId);

        var states = new HashMap<Long, SeatStatus>();

        var currentTimeMinusHoldDuration = LocalDateTime.now().minus(ReservationService.HOLD_DURATION);
        var activeReservations = reservations.findActiveByScreeningId(screeningId, currentTimeMinusHoldDuration);

        for (var reservation : activeReservations) {
            var status = reservation.getReservationStatus() == ReservationStatus.RESERVED
                    ? SeatStatus.RESERVED
                    : SeatStatus.CANDIDATE;

            for (var seat : reservation.getSeats()) {
                var previousStatus = states.get(seat.getId());

                if (previousStatus != SeatStatus.RESERVED) {
                    states.put(seat.getId(), status);
                }
            }
        }

        var result = new ArrayList<SeatDto>();

        for (var seat : seats.findByScreeningId(screeningId)) {
            var status = states.getOrDefault(seat.getId(), SeatStatus.FREE);

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
