package com.vdobrikov.booking.service;

import com.vdobrikov.booking.persistence.BookingEntity;
import com.vdobrikov.booking.persistence.BookingRepository;
import com.vdobrikov.model.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingEntity addPendingBooking(Booking booking) {
        BookingEntity bookingEntity = bookingRepository.save(new BookingEntity(booking, BookingEntity.Status.PENDING));
        log.info("New pending booking={}", bookingEntity);
        eventPublisher.publishEvent(booking);
        return bookingEntity;
    }

    public BookingEntity completeBooking(UUID id) {
        BookingEntity bookingEntity = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found id=" + id));
        bookingEntity = new BookingEntity(bookingEntity.booking(), BookingEntity.Status.COMPLETED);
        log.info("Complete booking={}", bookingEntity);
        return bookingRepository.save(bookingEntity);
    }

    public Optional<BookingEntity> rollbackBooking(UUID id) {
        log.info("Rollback booking id={}", id);
        return bookingRepository.delete(id);
    }

    public Collection<BookingEntity> findAll() {
        return bookingRepository.findAll();
    }
}
