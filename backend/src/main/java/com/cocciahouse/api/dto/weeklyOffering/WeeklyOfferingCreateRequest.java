package com.cocciahouse.api.dto.weeklyOffering;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WeeklyOfferingCreateRequest(

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate

) {
}