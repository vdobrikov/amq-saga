package com.vdobrikov.booking.messaging;

import com.vdobrikov.booking.service.BookingService;
import com.vdobrikov.model.ActionStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class ActionStatusHandler {
    private final BookingService bookingService;

    @JmsListener(destination = "${jms.topic.status}")
    public void onActionStatus(ActionStatus actionStatus) {
        // Here distributed transaction completes
        switch (actionStatus.status()) {
            case FAIL -> {
                log.warn("Action failed actionStatus={}", actionStatus);
                bookingService.rollbackBooking(actionStatus.id());
            }
            case SUCCESS -> {
                log.info("Action succeeded actionStatus={}", actionStatus);
                bookingService.completeBooking(actionStatus.id());
            }
        }
    }
}
