package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaSpecialtyAddOnPricingType;

import java.math.BigDecimal;

public record PizzaSpecialtyAddOnResponse(

        Long pizzaAddOnId,
        String name,
        BigDecimal regularAmount,
        PizzaSpecialtyAddOnPricingType pricingType,
        BigDecimal overrideAmount

) {}