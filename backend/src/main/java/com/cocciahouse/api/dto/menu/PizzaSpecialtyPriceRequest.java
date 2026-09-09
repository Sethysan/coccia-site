package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PizzaSpecialtyPriceRequest(

        @NotNull
        Long pizzaSizeId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount

) {
}