package com.vdobrikov.booking.web;

import com.vdobrikov.booking.persistence.BookingEntity;
import com.vdobrikov.booking.persistence.BookingRepository;
import com.vdobrikov.booking.service.BookingService;
import com.vdobrikov.model.Booking;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingEntity addBooking(@RequestBody @Valid BookingDto bookingDto) {
        Booking booking = new Booking(UUID.randomUUID(), bookingDto.name(), bookingDto.price());
        // Here distributed transaction begins
        return bookingService.addPendingBooking(booking);
    }

    @GetMapping
    public Collection<BookingEntity> listAllBookings() {
        return bookingService.findAll();
    }
}
