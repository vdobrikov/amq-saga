package com.vdobrikov.payment.messaging;

import com.vdobrikov.model.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingHandler {

    @JmsListener(destination = "booking", containerFactory = "defaultFactory")
    public void onBooking(Booking booking) {
        log.info("Received booking={}", booking);
    }
}
