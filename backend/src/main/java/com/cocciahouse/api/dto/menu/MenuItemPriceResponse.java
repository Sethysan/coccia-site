package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record MenuItemPriceResponse(

        Long id,
        String label,
        BigDecimal amount,
        int displayOrder

) {
}