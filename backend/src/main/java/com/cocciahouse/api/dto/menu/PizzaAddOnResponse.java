package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaAddOnType;

import java.math.BigDecimal;

public record PizzaAddOnResponse(

        Long id,

        Long menuSectionId,

        String name,

        BigDecimal amount,

        PizzaAddOnType type,

        int displayOrder,

        boolean active

) {
}