package com.vdobrikov.payment.event;

import com.vdobrikov.model.ActionStatus;
import com.vdobrikov.payment.properties.JmsTopics;
import com.vdobrikov.payment.service.PaymentEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentEventHandler {
    private final JmsTemplate jmsTemplate;
    private final JmsTopics jmsTopics;

    @Async
    @EventListener
    public void onPayment(PaymentEvent event) {
        switch (event.status()) {
            case SUCCESS -> {
                log.info("Payment succeeded for id={}", event.id());
                jmsTemplate.convertAndSend(jmsTopics.status(), new ActionStatus(event.id(), ActionStatus.Status.SUCCESS, event.comment()));
            }
            case FAIL -> {
                log.error("Payment failed for id={}", event.id());
                jmsTemplate.convertAndSend(jmsTopics.status(), new ActionStatus(event.id(), ActionStatus.Status.FAIL, event.comment()));
            }
        }
    }
}
