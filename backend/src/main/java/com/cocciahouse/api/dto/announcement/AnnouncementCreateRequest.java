package com.cocciahouse.api.dto.announcement;

import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.AnnouncementType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AnnouncementCreateRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        String message,

        @NotNull
        AnnouncementPlacement placement,

        @NotNull
        AnnouncementType type,

        Instant startDateTime,

        Instant endDateTime,

        Integer displayOrder,

        String imageUrl,

        String imageAlt
) {
}