package com.vdobrikov.booking.event;

import com.vdobrikov.model.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingHandler {
    private final JmsTemplate jmsTemplate;
    private final String topic;

    public BookingHandler(JmsTemplate jmsTemplate, @Value("${topic_booking}") String topic) {
        this.jmsTemplate = jmsTemplate;
        this.topic = topic;
    }

    @Async
    @EventListener
    public void onBooking(Booking booking) {
        log.info("New booking, sending to queue. booking={}", booking);
        jmsTemplate.convertAndSend(topic, booking);
    }
}
