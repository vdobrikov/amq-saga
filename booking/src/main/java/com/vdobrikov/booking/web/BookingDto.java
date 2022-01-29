package com.vdobrikov.booking.web;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class BookingDto {
    @NotBlank
    private String name;

    @Min(1)
    private int price;
}
