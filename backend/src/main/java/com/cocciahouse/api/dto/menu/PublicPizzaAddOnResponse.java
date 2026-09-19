package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaAddOnType;

import java.math.BigDecimal;

public record PublicPizzaAddOnResponse(

        String name,
        BigDecimal amount,
        PizzaAddOnType type

) {
}