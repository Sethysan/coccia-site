package com.cocciahouse.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WeeklyOfferingCreateRequest(

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate

) {
}