package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record PublicMenuItemPriceResponse(

        String label,
        BigDecimal amount

) {
}