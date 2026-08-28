package com.cocciahouse.api.dto.recipe;

import java.math.BigDecimal;

public record OfferingItemPriceResponse(
        Long id,
        String label,
        BigDecimal amount,
        int displayOrder
) {
}