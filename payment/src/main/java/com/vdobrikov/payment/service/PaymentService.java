package com.vdobrikov.payment.service;

import com.vdobrikov.model.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {
    public static final int MAX_PRICE = 100;
    private final ApplicationEventPublisher eventPublisher;

    public void makePaymentFor(Booking booking) {
        if (booking.price() > MAX_PRICE) {
            log.info("Payment failed booking={}", booking);
            eventPublisher.publishEvent(new PaymentEvent(booking.id(), PaymentEvent.Status.FAIL, "Too expensive"));
            return;
        }
        log.info("Payment successful id={}", booking.id());
        eventPublisher.publishEvent(new PaymentEvent(booking.id(), PaymentEvent.Status.SUCCESS, "Done"));
    }
}
