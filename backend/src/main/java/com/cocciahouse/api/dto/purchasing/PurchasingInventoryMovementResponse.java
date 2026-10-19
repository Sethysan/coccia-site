
package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchasingInventoryMovementResponse(
        Long id,
        Long productId,
        String productName,
        String movementType,
        BigDecimal quantityChange,
        String baseUnit,
        Long receiptItemId,
        String notes,
        LocalDateTime createdAt
) {
}
