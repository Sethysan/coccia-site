package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MenuSubsectionRequest(
        @NotBlank String name,

        @DecimalMin(
                value = "0.01",
                message = "Price must be greater than zero."
        )
        BigDecimal price,

        @NotNull Boolean active
) {}