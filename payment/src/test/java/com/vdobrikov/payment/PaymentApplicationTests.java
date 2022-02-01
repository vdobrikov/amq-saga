package com.vdobrikov.payment;

import com.vdobrikov.model.ActionStatus;
import com.vdobrikov.model.Booking;
import com.vdobrikov.payment.properties.JmsTopics;
import com.vdobrikov.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class PaymentApplicationTests {
    private static final UUID ID = UUID.randomUUID();

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsTopics jmsTopics;

    @Test
    void contextLoads() {
    }

    @Test
    void testPaymentSucceeded() {
        jmsTemplate.convertAndSend(jmsTopics.booking(), new Booking(ID, "name", PaymentService.MAX_PRICE - 1));
        awaitAllMessagesConsumedFor(jmsTopics.booking());

        Object message = jmsTemplate.receiveAndConvert(jmsTopics.status());
        assertThat(message)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", ID)
                .hasFieldOrPropertyWithValue("status", ActionStatus.Status.SUCCESS);
    }

    @Test
    void testPaymentFailed() {
        jmsTemplate.convertAndSend(jmsTopics.booking(), new Booking(ID, "name", PaymentService.MAX_PRICE + 1));
        awaitAllMessagesConsumedFor(jmsTopics.booking());

        Object message = jmsTemplate.receiveAndConvert(jmsTopics.status());
        assertThat(message)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", ID)
                .hasFieldOrPropertyWithValue("status", ActionStatus.Status.FAIL);
    }

    private void awaitAllMessagesConsumedFor(String topic) {
        await()
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> jmsTemplate.browseSelected(topic, "true = true",
                        (s, qb) -> !qb.getEnumeration().hasMoreElements()));
    }
}
