package cz.swi.cinema;

import cz.swi.cinema.models.*;
import cz.swi.cinema.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MovieRepository movieRepo, RoomRepository roomRepo, ScreeningRepository screeningRepo, SeatRepository seatRepo, UserRepository userRepo) {
        return args -> {
            // Mock User (frontend currently uses userId 1)
            User u1 = new User(); u1.setFirstName("Test"); u1.setLastName("User"); u1.setEmail("test@example.com"); u1.setAge(30);
            userRepo.save(u1);

            // Mock Movies
            Movie m1 = new Movie(); m1.setName("Inception"); m1.setDirector("Nolan"); m1.setDuration(148L);
            movieRepo.save(m1);
            Movie m2 = new Movie(); m2.setName("Interstellar"); m2.setDirector("Nolan"); m2.setDuration(169L);
            movieRepo.save(m2);

            // Mock Rooms
            Room r1 = new Room(); r1.setName("Hall A");
            roomRepo.save(r1);
            Room r2 = new Room(); r2.setName("Hall B");
            roomRepo.save(r2);

            // Mock Seats
            for (int i = 1; i <= 10; i++) {
                Seat s = new Seat();
                s.setRoom(r1);
                s.setStatus(cz.swi.cinema.enums.SeatStatus.FREE);
                seatRepo.save(s);
            }

            // Mock Screenings
            Screening s1 = new Screening();
            s1.setMovie(m1);
            s1.setRoom(r1);
            s1.setPrice(new BigDecimal("15.00"));
            s1.setTime(LocalDateTime.now().plusDays(1).withHour(20).withMinute(0));
            screeningRepo.save(s1);

            Screening s2 = new Screening();
            s2.setMovie(m2);
            s2.setRoom(r2);
            s2.setPrice(new BigDecimal("20.00"));
            s2.setTime(LocalDateTime.now().plusDays(2).withHour(21).withMinute(30));
            screeningRepo.save(s2);

            System.out.println("Database initialized with sample data.");
        };
    }
}
