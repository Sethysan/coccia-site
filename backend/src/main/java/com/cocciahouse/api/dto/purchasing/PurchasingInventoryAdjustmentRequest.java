package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;

public record PurchasingInventoryAdjustmentRequest(
        String movementType,
        BigDecimal quantity,
        String notes
) {
}
