package cz.swi.cinema.models;

import cz.swi.shared.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;

    @Column(nullable = false)
    private String contactEmail;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Payment payment;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @ManyToMany
    @JoinTable(
            name = "seat_reservation",
            joinColumns = { @JoinColumn(name = "reservation_id") },
            inverseJoinColumns = { @JoinColumn(name = "seat_id") }
    )
    private List<Seat> seats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

}
