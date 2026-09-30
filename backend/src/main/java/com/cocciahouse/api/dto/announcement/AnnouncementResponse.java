package com.cocciahouse.api.dto.announcement;

import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.AnnouncementStatus;
import com.cocciahouse.api.model.AnnouncementType;

import java.time.Instant;

public record AnnouncementResponse(
        Long id,
        String title,
        String message,
        AnnouncementPlacement placement,
        AnnouncementType type,
        AnnouncementStatus status,
        Instant startDateTime,
        Instant endDateTime,
        Integer displayOrder,
        String imageUrl,
        String imageAlt,
        Instant createdAt,
        Instant updatedAt
) {
}