package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemPriceRequest(

        @Size(
                max = 100,
                message = "Price label must be 100 characters or fewer."
        )
        String label,

        @NotNull(message = "Price amount is required.")
        @DecimalMin(
                value = "0.01",
                message = "Price amount must be greater than zero."
        )
        BigDecimal amount,

        @NotNull(message = "Price display order is required.")
        @Min(
                value = 0,
                message = "Price display order cannot be negative."
        )
        Integer displayOrder

) {
}