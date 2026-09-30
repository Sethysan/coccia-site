package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record PublicPizzaSizeResponse(

        String name,
        BigDecimal basePrice

) {
}