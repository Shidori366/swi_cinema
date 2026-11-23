package cz.swi.cinema;

import cz.swi.cinema.enums.PaymentStatus;
import cz.swi.cinema.enums.ReservationStatus;
import cz.swi.cinema.enums.SeatStatus;
import cz.swi.cinema.models.*;
import cz.swi.cinema.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    /** Seats per row — must match the seat labelling in ScreeningService. */
    public static final int SEATS_PER_ROW = 10;

    @Bean
    CommandLineRunner initDatabase(MovieRepository movieRepo,
                                   RoomRepository roomRepo,
                                   ScreeningRepository screeningRepo,
                                   SeatRepository seatRepo,
                                   UserRepository userRepo,
                                   ReservationRepository reservationRepo,
                                   PaymentRepository paymentRepo) {
        return args -> {
            // The DB file persists between runs — only seed an empty database.
            if (movieRepo.count() > 0 || roomRepo.count() > 0) {
                System.out.println("Database already contains data, skipping seed.");
                return;
            }

            // ---------- Users (frontend currently uses userId 1) ----------
            User testUser = user("Test", "User", "test@example.com", 30, userRepo);
            User jana = user("Jana", "Novakova", "jana.novakova@example.com", 27, userRepo);
            User petr = user("Petr", "Svoboda", "petr.svoboda@example.com", 41, userRepo);

            // ---------- Movies ----------
            Movie inception    = movie("Inception", "Christopher Nolan", 148L, movieRepo);
            Movie interstellar = movie("Interstellar", "Christopher Nolan", 169L, movieRepo);
            Movie duneTwo      = movie("Dune: Part Two", "Denis Villeneuve", 166L, movieRepo);
            Movie spiritedAway = movie("Spirited Away", "Hayao Miyazaki", 125L, movieRepo);
            Movie parasite     = movie("Parasite", "Bong Joon-ho", 132L, movieRepo);
            Movie matrix       = movie("The Matrix", "Lana & Lilly Wachowski", 136L, movieRepo);

            // ---------- Rooms + seats (rows of 10) ----------
            Room hallA = room("Hall A", 5, roomRepo, seatRepo); // 50 seats
            Room hallB = room("Hall B", 4, roomRepo, seatRepo); // 40 seats
            Room hallC = room("Hall C", 3, roomRepo, seatRepo); // 30 seats (small hall)

            // ---------- Screenings over the next 4 days ----------
            LocalDate today = LocalDate.now();
            Screening s1 = screening(inception,    hallA, today.plusDays(1), 18, 0,  "189", screeningRepo);
            Screening s2 = screening(interstellar, hallB, today.plusDays(1), 20, 30, "209", screeningRepo);
            Screening s3 = screening(duneTwo,      hallA, today.plusDays(1), 21, 15, "229", screeningRepo);
            screening(spiritedAway, hallC, today.plusDays(2), 15, 0,  "149", screeningRepo);
            Screening s5 = screening(parasite,     hallB, today.plusDays(2), 19, 0,  "169", screeningRepo);
            screening(matrix,       hallC, today.plusDays(2), 21, 45, "159", screeningRepo);
            screening(interstellar, hallA, today.plusDays(3), 17, 30, "209", screeningRepo);
            screening(inception,    hallB, today.plusDays(3), 20, 0,  "189", screeningRepo);
            screening(duneTwo,      hallA, today.plusDays(4), 19, 30, "229", screeningRepo);
            screening(spiritedAway, hallC, today.plusDays(4), 16, 0,  "149", screeningRepo);

            // ---------- Sample reservations (so some seats show as taken) ----------
            reservation(testUser, s1, seats(hallA, seatRepo, 22, 23), ReservationStatus.RESERVED, reservationRepo, paymentRepo);
            reservation(testUser, s2, seats(hallB, seatRepo, 14, 15, 16), ReservationStatus.PENDING, reservationRepo, paymentRepo);
            reservation(jana,     s1, seats(hallA, seatRepo, 24, 25, 26, 27), ReservationStatus.RESERVED, reservationRepo, paymentRepo);
            reservation(petr,     s2, seats(hallB, seatRepo, 4, 5), ReservationStatus.RESERVED, reservationRepo, paymentRepo);
            reservation(jana,     s3, seats(hallA, seatRepo, 34, 35), ReservationStatus.PENDING, reservationRepo, paymentRepo);
            reservation(petr,     s5, seats(hallB, seatRepo, 1, 2), ReservationStatus.CANCELLED, reservationRepo, paymentRepo);

            System.out.println("Database initialized with sample data.");
        };
    }

    private static User user(String first, String last, String email, int age, UserRepository repo) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setEmail(email);
        u.setAge(age);
        return repo.save(u);
    }

    private static Movie movie(String name, String director, long duration, MovieRepository repo) {
        Movie m = new Movie();
        m.setName(name);
        m.setDirector(director);
        m.setDuration(duration);
        return repo.save(m);
    }

    private static Room room(String name, int rows, RoomRepository roomRepo, SeatRepository seatRepo) {
        Room r = new Room();
        r.setName(name);
        r = roomRepo.save(r);
        for (int i = 0; i < rows * SEATS_PER_ROW; i++) {
            Seat s = new Seat();
            s.setRoom(r);
            s.setStatus(SeatStatus.FREE);
            seatRepo.save(s);
        }
        return r;
    }

    private static Screening screening(Movie movie, Room room, LocalDate day, int hour, int minute,
                                       String price, ScreeningRepository repo) {
        Screening s = new Screening();
        s.setMovie(movie);
        s.setRoom(room);
        s.setPrice(new BigDecimal(price));
        s.setTime(LocalDateTime.of(day, LocalTime.of(hour, minute)));
        return repo.save(s);
    }

    /** Picks seats of a room by their 1-based position (ordered by id). */
    private static List<Seat> seats(Room room, SeatRepository repo, int... positions) {
        List<Seat> all = repo.findByRoomId(room.getId());
        all.sort((a, b) -> a.getId().compareTo(b.getId()));
        List<Seat> picked = new ArrayList<>();
        for (int p : positions) picked.add(all.get(p - 1));
        return picked;
    }

    private static void reservation(User user, Screening screening, List<Seat> seats, ReservationStatus status,
                                    ReservationRepository reservationRepo, PaymentRepository paymentRepo) {
        Reservation r = new Reservation();
        r.setUser(user);
        r.setContactEmail(user.getEmail());
        r.setScreening(screening);
        r.setSeats(seats);
        r.setReservationStatus(status);
        r = reservationRepo.save(r);

        Payment p = new Payment();
        p.setReservation(r);
        p.setAmount(screening.getPrice().multiply(BigDecimal.valueOf(seats.size())));
        p.setPaymentStatus(status == ReservationStatus.RESERVED ? PaymentStatus.COMPLETED : PaymentStatus.PENDING);
        paymentRepo.save(p);
    }
}
