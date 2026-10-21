
package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;
import java.time.Instant;

public record PurchasingVendorProductResponse(
        Long id,
        Long productId,
        Long vendorId,
        String vendorSku,
        String orderUnit,
        BigDecimal packageQuantity,
        String packageUnit,
        BigDecimal unitPrice,
        boolean preferred,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
