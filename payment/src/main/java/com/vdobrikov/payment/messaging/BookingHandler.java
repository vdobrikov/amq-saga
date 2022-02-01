package com.vdobrikov.payment.messaging;

import com.vdobrikov.model.Booking;
import com.vdobrikov.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class BookingHandler {
    private final PaymentService paymentService;

    @JmsListener(destination = "${jms.topic.booking}")
    public void onBooking(Booking booking) {
        log.info("Received booking={}", booking);
        paymentService.makePaymentFor(booking);
    }
}
