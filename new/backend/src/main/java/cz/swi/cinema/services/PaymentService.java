package cz.swi.cinema.services;

import cz.swi.cinema.enums.PaymentStatus;
import cz.swi.cinema.models.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public boolean processPayment(Payment payment) {
        // Mocked: always succeeds
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        return true;
    }
}
