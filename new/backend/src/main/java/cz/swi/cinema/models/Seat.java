package cz.swi.cinema.models;

import cz.swi.cinema.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.FREE;

    @ManyToMany(mappedBy = "seats")
    private List<Reservation> reservations = new ArrayList<>();
}
