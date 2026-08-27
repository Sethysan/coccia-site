package com.cocciahouse.api.dto.hours;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record RestaurantHoursUpdateRequest(

        @NotNull
        Integer dayOfWeek,

        @NotNull
        Boolean closed,

        LocalTime openTime,

        LocalTime closeTime,

        @Size(max = 255)
        String note
) {
}