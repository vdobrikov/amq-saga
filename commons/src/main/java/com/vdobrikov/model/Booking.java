package com.vdobrikov.model;

import java.util.UUID;

public record Booking(UUID id, String name, int price) {
}
