package com.cocciahouse.api.dto;

import java.math.BigDecimal;

public record OfferingItemPriceResponse(
        Long id,
        String label,
        BigDecimal amount,
        int displayOrder
) {
}