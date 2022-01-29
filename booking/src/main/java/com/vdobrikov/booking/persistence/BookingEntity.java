package com.vdobrikov.booking.persistence;

import com.vdobrikov.model.Booking;

public record BookingEntity(Booking booking, Status status) {
    public enum Status {
        PENDING,
        COMPLETED
    }
}
