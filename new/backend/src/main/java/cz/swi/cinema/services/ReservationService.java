package cz.swi.cinema.services;

import cz.swi.cinema.dto.ReservationDto;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.models.*;
import cz.swi.cinema.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Transactional
    public ReservationDto createReservation(cz.swi.cinema.dto.CreateReservationRequestDto dto) {
        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new cz.swi.cinema.exceptions.ResourceNotFoundException("Screening not found"));

        List<Seat> requestedSeats = new ArrayList<>();
        for (Long seatId : dto.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new cz.swi.cinema.exceptions.ResourceNotFoundException("Seat not found"));
            
            boolean takenForScreening = reservationRepository.findBySeatId(seatId).stream()
                    .anyMatch(r -> r.getScreening().getId().equals(screening.getId())
                            && r.getReservationStatus() != ReservationStatus.CANCELLED);
            if (takenForScreening) {
                throw new RuntimeException("Seat is already reserved");
            }
            
            seat.setStatus(cz.swi.cinema.enums.SeatStatus.CANDIDATE);
            requestedSeats.add(seat);
            seatRepository.save(seat);
        }

        Reservation reservation = new Reservation();
        reservation.setScreening(screening);
        reservation.setSeats(requestedSeats);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setContactEmail("user@example.com"); 
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new cz.swi.cinema.exceptions.ResourceNotFoundException("User not found"));
            reservation.setUser(user);
        }

        Reservation saved = reservationRepository.save(reservation);
        
        Payment payment = new Payment();
        payment.setReservation(saved);
        payment.setAmount(screening.getPrice());
        payment.setPaymentStatus(cz.swi.cinema.enums.PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return convertToDto(saved);
    }

    @Transactional
    public ReservationDto payReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new cz.swi.cinema.exceptions.ResourceNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Reservation is not in PENDING state");
        }

        Payment payment = reservation.getPayment();
        if (paymentService.processPayment(payment)) {
            payment.setPaymentStatus(cz.swi.cinema.enums.PaymentStatus.COMPLETED);
            reservation.setReservationStatus(ReservationStatus.RESERVED);
            
            for (Seat seat : reservation.getSeats()) {
                seat.setStatus(cz.swi.cinema.enums.SeatStatus.RESERVED);
                seatRepository.save(seat);
            }
            
            paymentRepository.save(payment);
            reservationRepository.save(reservation);
        }

        return convertToDto(reservation);
    }

    @Transactional
    public ReservationDto cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new cz.swi.cinema.exceptions.ResourceNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        for (Seat seat : reservation.getSeats()) {
            seat.setStatus(cz.swi.cinema.enums.SeatStatus.FREE);
            seatRepository.save(seat);
        }

        reservationRepository.save(reservation);
        return convertToDto(reservation);
    }

    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream().map(this::convertToDto).toList();
    }

    public List<ReservationDto> getReservationsByUser(Long userId) {
        return reservationRepository.findByUser_Id(userId).stream().map(this::convertToDto).toList();
    }

    private ReservationDto convertToDto(Reservation r) {
        cz.swi.cinema.dto.ReservationDto dto = new cz.swi.cinema.dto.ReservationDto();
        dto.setId(r.getId());
        dto.setContactEmail(r.getContactEmail());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setScreeningId(r.getScreening().getId());
        dto.setStatus(r.getReservationStatus() != null ? r.getReservationStatus().name() : null);
        dto.setSeatIds(r.getSeats().stream().map(Seat::getId).toList());
        return dto;
    }
}