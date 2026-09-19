package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
