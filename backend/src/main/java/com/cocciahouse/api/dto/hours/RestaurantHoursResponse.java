package com.cocciahouse.api.dto.hours;

import java.time.Instant;
import java.time.LocalTime;

public record RestaurantHoursResponse(
        Long id,
        Integer dayOfWeek,
        String dayName,
        boolean closed,
        LocalTime openTime,
        LocalTime closeTime,
        String note,
        Integer displayOrder,
        Instant updatedAt
) {
}