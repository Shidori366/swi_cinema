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
        
        return seatRepository.findByRoomId(s.getRoom().getId()).stream().map(seat -> {
            SeatDto dto = new SeatDto();
            dto.setId(seat.getId());
            dto.setStatus(seat.getStatus().name());
            dto.setSeatNumber(String.valueOf(seat.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    public List<SeatDto> getAvailableSeatsForScreening(Long screeningId) {
        Screening s = screeningRepository.findById(screeningId)
            .orElseThrow(() -> new RuntimeException("Screening not found"));
        
        List<Seat> allSeats = seatRepository.findByRoomId(s.getRoom().getId());
        
        // A seat is available if it's not part of any non-cancelled reservation
        List<Seat> availableSeats = allSeats.stream()
            .filter(seat -> reservationRepository.findBySeatId(seat.getId()).stream()
                .noneMatch(r -> r.getReservationStatus() != cz.swi.cinema.enums.ReservationStatus.CANCELLED))
            .toList();

        return availableSeats.stream().map(seat -> {
            SeatDto dto = new SeatDto();
            dto.setId(seat.getId());
            dto.setStatus(seat.getStatus().name());
            dto.setSeatNumber(String.valueOf(seat.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
