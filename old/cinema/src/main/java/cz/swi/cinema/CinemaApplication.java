package cz.swi.cinema;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CinemaApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Prague"));
	}

	public static void main(String[] args) {
		SpringApplication.run(CinemaApplication.class, args);
	}

}
