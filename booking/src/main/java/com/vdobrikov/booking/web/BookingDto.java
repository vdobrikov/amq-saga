package com.vdobrikov.booking.web;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record BookingDto(
    @NotBlank
    String name,

    @Min(1)
    int price
) {}
