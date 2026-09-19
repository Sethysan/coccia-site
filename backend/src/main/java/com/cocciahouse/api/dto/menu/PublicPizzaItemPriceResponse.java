package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record PublicPizzaItemPriceResponse(

        String sizeName,
        BigDecimal amount

) {
}