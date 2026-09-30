package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record PizzaSpecialtyPriceResponse(

        Long id,

        Long pizzaSizeId,

        String pizzaSizeName,

        BigDecimal amount

) {
}
