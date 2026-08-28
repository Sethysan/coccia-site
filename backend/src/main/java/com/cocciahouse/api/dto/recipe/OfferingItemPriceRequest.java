package com.cocciahouse.api.dto.recipe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OfferingItemPriceRequest(

        String label,

        @NotNull
        @Positive
        BigDecimal amount,

        int displayOrder

) {
}