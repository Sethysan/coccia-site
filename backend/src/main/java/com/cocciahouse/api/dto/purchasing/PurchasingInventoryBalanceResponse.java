
package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchasingInventoryBalanceResponse(
        Long productId,
        String productName,
        String category,
        String baseUnit,
        BigDecimal quantityOnHand,
        LocalDateTime updatedAt
) {
}
