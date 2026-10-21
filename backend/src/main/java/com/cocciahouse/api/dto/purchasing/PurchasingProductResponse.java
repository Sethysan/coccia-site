
package com.cocciahouse.api.dto.purchasing;

import java.time.Instant;

public record PurchasingProductResponse(
        Long id,
        Long departmentId,
        Long categoryId,
        String name,
        String description,
        boolean active,
        String baseUnit,
        Instant createdAt,
        Instant updatedAt
) {
}
