package com.vdobrikov.payment.service;

import java.util.UUID;

public record PaymentEvent(UUID id, Status status, String comment) {
    public enum Status {
        SUCCESS,
        FAIL
    }
}
