package com.vdobrikov.model;

import java.util.UUID;

public record ActionStatus(UUID id, Status status, String info) {
    public enum Status {
        SUCCESS,
        FAIL
    }
}
