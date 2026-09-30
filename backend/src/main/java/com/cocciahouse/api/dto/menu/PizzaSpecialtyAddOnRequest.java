package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaSpecialtyAddOnPricingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PizzaSpecialtyAddOnRequest(

        @NotNull
        Long pizzaAddOnId,

        @NotNull
        PizzaSpecialtyAddOnPricingType pricingType,

        @DecimalMin(value = "0.00")
        BigDecimal overrideAmount

) {}