package com.vdobrikov.booking.web;

import com.vdobrikov.booking.persistence.BookingRepository;
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
    private final BookingRepository bookingRepository;

    @PostMapping
    public Booking addBooking(@RequestBody @Valid BookingDto booking) {
        Booking bookingEntity = new Booking(UUID.randomUUID(), booking.getName(), booking.getPrice());
        return bookingRepository.save(bookingEntity);
    }

    @GetMapping
    public Collection<Booking> listAllBookings() {
        return bookingRepository.findAll();
    }
}
