package com.cocciahouse.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WeeklyOfferingResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        List<WeeklyOfferingItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {
}