package com.cocciahouse.api.dto;

import com.cocciahouse.api.model.WeeklyOfferingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WeeklyOfferingResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        WeeklyOfferingStatus status,
        List<WeeklyOfferingItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {
}