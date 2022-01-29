package com.vdobrikov.booking.persistence;

import com.vdobrikov.model.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@AllArgsConstructor
@Component
public class BookingRepository {
    private final ApplicationEventPublisher eventPublisher;
    private final Map<UUID, Booking> idToBooking = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        if (booking.id() == null) {
            booking = new Booking(UUID.randomUUID(), booking.name(), booking.price());
        }
        log.info("Saving booking={}", booking);
        idToBooking.put(booking.id(), booking);
        eventPublisher.publishEvent(booking);
        return booking;
    }

    public Collection<Booking> findAll() {
        return idToBooking.values();
    }

    public Optional<Booking> findById(UUID id) {
        return Optional.ofNullable(idToBooking.get(id));
    }

    public Optional<Booking> delete(UUID id) {
        log.info("Deleting id={}", id);
        return Optional.ofNullable(idToBooking.remove(id));
    }
}
