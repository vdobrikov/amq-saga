package com.vdobrikov.booking;

import com.vdobrikov.booking.persistence.BookingEntity;
import com.vdobrikov.booking.persistence.BookingRepository;
import com.vdobrikov.booking.properties.JmsTopics;
import com.vdobrikov.booking.web.BookingDto;
import com.vdobrikov.model.ActionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingApplicationTests {
    private static final String NAME = "test-name";
    private static final int PRICE = 42;
    private static final String PATH_BOOKING = "/booking";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsTopics jmsTopics;

    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testPendingBooking() {
        // Add pending booking
        ResponseEntity<BookingEntity> response = restTemplate.postForEntity(PATH_BOOKING, createTestBookingDto(), BookingEntity.class);
        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .hasFieldOrPropertyWithValue("status", BookingEntity.Status.PENDING);

        // Verify that booking message was sent
        jmsTemplate.setReceiveTimeout(1000);
        Object message = jmsTemplate.receiveAndConvert(jmsTopics.booking());
        assertThat(message)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", NAME)
                .hasFieldOrPropertyWithValue("price", PRICE)
                .extracting("id")
                .isNotNull();
    }

    @Test
    void testOrderCompleted() {
        // Add pending booking
        ResponseEntity<BookingEntity> responseBooking = restTemplate.postForEntity(PATH_BOOKING, createTestBookingDto(), BookingEntity.class);
        UUID id = responseBooking.getBody().booking().id();

        // Emulate succeeded payment
        jmsTemplate.convertAndSend(jmsTopics.status(), new ActionStatus(id, ActionStatus.Status.SUCCESS, "test-comment"));
        awaitAllMessagesConsumedFor(jmsTopics.status());

        // Verify updated status of booking
        ResponseEntity<BookingEntity[]> responseBookings = restTemplate.getForEntity(PATH_BOOKING, BookingEntity[].class);
        assertThat(responseBookings)
                .isNotNull();
        BookingEntity[] bookings = responseBookings.getBody();
        assertThat(bookings)
                .hasSize(1)
                .hasOnlyOneElementSatisfying(e -> {
                    assertThat(e.status()).isEqualTo(BookingEntity.Status.COMPLETED);
                    assertThat(e.booking().id()).isEqualTo(id);
                });
    }

    @Test
    void testOrderFailed() {
        // Add pending booking
        ResponseEntity<BookingEntity> responseBooking = restTemplate.postForEntity(PATH_BOOKING, createTestBookingDto(), BookingEntity.class);
        UUID id = responseBooking.getBody().booking().id();

        // Emulate failed payment
        jmsTemplate.convertAndSend(jmsTopics.status(), new ActionStatus(id, ActionStatus.Status.FAIL, "test-comment"));
        awaitAllMessagesConsumedFor(jmsTopics.status());

        // Verify that booking was deleted
        ResponseEntity<BookingEntity[]> responseBookings = restTemplate.getForEntity(PATH_BOOKING, BookingEntity[].class);
        assertThat(responseBookings)
                .isNotNull();
        BookingEntity[] bookings = responseBookings.getBody();
        assertThat(bookings)
                .isEmpty();
    }

    private void awaitAllMessagesConsumedFor(String topic) {
        await()
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> jmsTemplate.browseSelected(topic, "true = true",
                        (s, qb) -> !qb.getEnumeration().hasMoreElements()));
    }

    private BookingDto createTestBookingDto() {
        return new BookingDto(NAME, PRICE);
    }
}
