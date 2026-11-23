package cz.swi.cinema.repositories;

import cz.swi.cinema.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
