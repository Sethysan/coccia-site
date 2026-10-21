package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;

public record PurchasingOrderItemResponse(

        Long id,
        Long vendorProductId,
        BigDecimal plannedQuantity,
        BigDecimal estimatedUnitPrice,
        BigDecimal confirmedQuantity,
        BigDecimal confirmedUnitPrice,
        String orderUnit,
        BigDecimal packageQuantity,
        String packageUnit
) {
}