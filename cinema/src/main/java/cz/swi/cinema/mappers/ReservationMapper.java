package cz.swi.cinema.mappers;

import cz.swi.cinema.dto.ReservationDto;
import cz.swi.cinema.models.Reservation;
import cz.swi.cinema.models.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "id", source = "reservation.id")
    @Mapping(target = "screeningId", source = "reservation.screening.id")
    @Mapping(target = "seatIds", source = "reservation.seats")
    @Mapping(target = "status", source = "reservation.reservationStatus")
    @Mapping(target = "expiresAt", source = "expiresAt")
    ReservationDto toDto(Reservation reservation, LocalDateTime expiresAt);

    default Long seatToId(Seat seat) {
        return seat != null ? seat.getId() : null;
    }
}
