package com.vdobrikov.booking.persistence;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BookingRepository {
    private final Map<UUID, BookingEntity> idToBooking = new ConcurrentHashMap<>();

    public BookingEntity save(BookingEntity booking) {
        idToBooking.put(booking.booking().id(), booking);
        return booking;
    }

    public Collection<BookingEntity> findAll() {
        return idToBooking.values();
    }

    public Optional<BookingEntity> findById(UUID id) {
        return Optional.ofNullable(idToBooking.get(id));
    }

    public Optional<BookingEntity> delete(UUID id) {
        return Optional.ofNullable(idToBooking.remove(id));
    }

    public void deleteAll() {
        idToBooking.clear();
    }
}
