package cz.swi.cinema.services;

import cz.swi.cinema.dto.ScreeningDto;
import cz.swi.cinema.dto.SeatDto;
import cz.swi.cinema.models.Screening;
import cz.swi.cinema.models.Seat;
import cz.swi.cinema.repositories.ReservationRepository;
import cz.swi.cinema.repositories.ScreeningRepository;
import cz.swi.cinema.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public List<ScreeningDto> getAllScreenings() {
        return screeningRepository.findAll().stream().map(s -> {
            ScreeningDto dto = new ScreeningDto();
            dto.setId(s.getId());
            dto.setTime(s.getTime());
            dto.setMovieName(s.getMovie().getName());
            dto.setRoomName(s.getRoom().getName());
            dto.setPrice(s.getPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    public ScreeningDto getScreeningById(Long id) {
        Screening s = screeningRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Screening not found"));
        ScreeningDto dto = new ScreeningDto();
        dto.setId(s.getId());
        dto.setTime(s.getTime());
        dto.setMovieName(s.getMovie().getName());
        dto.setRoomName(s.getRoom().getName());
        dto.setPrice(s.getPrice());
        return dto;
    }

    public List<SeatDto> getSeatsForScreening(Long screeningId) {
        Screening s = screeningRepository.findById(screeningId)
            .orElseThrow(() -> new RuntimeException("Screening not found"));

        return toSeatDtos(roomSeatsOrdered(s.getRoom().getId()), seat -> true);
    }

    public List<SeatDto> getAvailableSeatsForScreening(Long screeningId) {
        Screening s = screeningRepository.findById(screeningId)
            .orElseThrow(() -> new RuntimeException("Screening not found"));

        // A seat is available if it's not part of any non-cancelled reservation for THIS screening
        return toSeatDtos(roomSeatsOrdered(s.getRoom().getId()), seat ->
            reservationRepository.findBySeatId(seat.getId()).stream()
                .noneMatch(r -> r.getScreening().getId().equals(screeningId)
                    && r.getReservationStatus() != cz.swi.cinema.enums.ReservationStatus.CANCELLED));
    }

    private List<Seat> roomSeatsOrdered(Long roomId) {
        List<Seat> seats = new java.util.ArrayList<>(seatRepository.findByRoomId(roomId));
        seats.sort(java.util.Comparator.comparing(Seat::getId));
        return seats;
    }

    /** Maps seats to DTOs with a human label like "A1", "B7" (rows of SEATS_PER_ROW, ordered by id). */
    private List<SeatDto> toSeatDtos(List<Seat> orderedRoomSeats, java.util.function.Predicate<Seat> include) {
        int perRow = cz.swi.cinema.DataInitializer.SEATS_PER_ROW;
        List<SeatDto> result = new java.util.ArrayList<>();
        for (int i = 0; i < orderedRoomSeats.size(); i++) {
            Seat seat = orderedRoomSeats.get(i);
            if (!include.test(seat)) continue;
            SeatDto dto = new SeatDto();
            dto.setId(seat.getId());
            dto.setStatus(seat.getStatus().name());
            dto.setSeatNumber("" + (char) ('A' + i / perRow) + (i % perRow + 1));
            result.add(dto);
        }
        return result;
    }
}
