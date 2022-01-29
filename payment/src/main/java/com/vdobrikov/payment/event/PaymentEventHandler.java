package com.vdobrikov.payment.event;

import com.vdobrikov.model.ActionStatus;
import com.vdobrikov.payment.service.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventHandler {
    private final JmsTemplate jmsTemplate;
    private final String topic;

    public PaymentEventHandler(JmsTemplate jmsTemplate, @Value("${topic_status}") String topic) {
        this.jmsTemplate = jmsTemplate;
        this.topic = topic;
    }

    @Async
    @EventListener
    public void onPayment(PaymentEvent event) {
        switch (event.status()) {
            case SUCCESS -> {
                log.info("Payment succeeded for id={}", event.id());
                jmsTemplate.convertAndSend(topic, new ActionStatus(event.id(), ActionStatus.Status.SUCCESS, event.comment()));
            }
            case FAIL -> {
                log.error("Payment failed for id={}", event.id());
                jmsTemplate.convertAndSend(topic, new ActionStatus(event.id(), ActionStatus.Status.FAIL, event.comment()));
            }
        }
    }
}
