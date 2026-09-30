package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PizzaSizeRequest(

        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal basePrice,

        @NotNull
        Boolean active

) {
}