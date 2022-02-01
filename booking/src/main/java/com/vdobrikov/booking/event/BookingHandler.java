package com.vdobrikov.booking.event;

import com.vdobrikov.booking.properties.JmsTopics;
import com.vdobrikov.model.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class BookingHandler {
    private final JmsTemplate jmsTemplate;
    private final JmsTopics jmsTopics;

    @Async
    @EventListener
    public void onBooking(Booking booking) {
        log.info("New booking, sending to queue. booking={}", booking);
        jmsTemplate.convertAndSend(jmsTopics.booking(), booking);
    }
}

