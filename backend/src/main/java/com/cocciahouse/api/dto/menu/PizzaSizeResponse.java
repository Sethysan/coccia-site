package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record PizzaSizeResponse(

        Long id,

        Long menuSectionId,

        String name,

        BigDecimal basePrice,

        int displayOrder,

        boolean active

) {
}